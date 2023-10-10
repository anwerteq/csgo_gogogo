package com.chenerzhu.crawler.proxy.buff.service;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.entity.steamInventory.ManualPlusRoot;
import com.chenerzhu.crawler.proxy.buff.entity.steamInventory.SteamInventoryRoot;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.Items;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Assets;
import com.chenerzhu.crawler.proxy.steam.service.SteamBuyItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * buff上架service
 */
@Service
@Slf4j
public class SteamInventorySerivce {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SteamBuyItemService steamBuyItemService;


    /**
     * 获取buff中可出售的库存数据
     */
    public List<Items> steamInventory() {
        //查询的为可交易的
        String url = "https://buff.163.com/api/market/steam_inventory?game=csgo&force=1&page_num=1&page_size=50&search=&state=cansell&_=" + System.currentTimeMillis();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        SteamInventoryRoot steamInventoryRoot = JSONObject.parseObject(responseEntity.getBody(), SteamInventoryRoot.class);
        List<Items> items = steamInventoryRoot.getData().getItems();
        return items;
    }

    /**
     * 自动上架逻辑
     */
    public void autoSale() {
        List<Items> items = steamInventory();
        //建立 assetid-classid-instanceid：paintwear的关系
        Map<String, String> keyAndPaintwear = items.stream().collect(Collectors.toMap(Items::getAssetidClassidInstanceid, item -> item.getAsset_info().getPaintwear()));
        List<Assets> assets = changeAssets(items);
        for (Assets asset : assets) {
            if (StrUtil.isNotEmpty(asset.getPrice())) {
                continue;
            }
            //有磨损度的饰品
            String paintwear = keyAndPaintwear.getOrDefault(asset.getAssetidClassidInstanceid(), "");
            if (StrUtil.isEmpty(paintwear)) {
                //以防万一
                assets.remove(asset);
            }


        }
    }


    /**
     * 根据磨损度获取售卖列表的价格
     *
     * @return
     */
    private String getSellPrice(String goods_id, String paintwear) {
        String paintwearInterval = getPaintwearInterval(paintwear);
        return "";
    }


    /**
     * 获取buff的售卖区间
     *
     * @param paintwear
     * @return
     */
    public String getPaintwearInterval(String paintwear) {
        List<Double> paintwears = new ArrayList() {{
            //崭新
            add(0.00);
            add(0.01);
            add(0.02);
            add(0.03);
            add(0.04);
            add(0.07);
            //略有磨损
            add(0.08);
            add(0.09);
            add(0.10);
            add(0.11);
            add(0.12);
            add(0.13);
            add(0.14);
            //久经沙场
            add(0.15);
            add(0.18);
            add(0.21);
            add(0.24);
            add(0.27);
            //破损不堪
            add(0.38);
            add(0.39);
            add(0.40);
            add(0.41);
            add(0.42);
            add(0.45);
            //战痕累累 每个饰品区间不一样
            add(0.50);
            add(0.60);
            add(0.70);
            add(0.80);
            add(0.90);
            add(1.0);
        }};
        Double paintwearF = Double.valueOf(paintwear);
        for (int i = 0; i < paintwears.size(); i++) {
            Double aDouble = paintwears.get(i);
            if (aDouble > paintwearF) {
                String interVal = paintwears.get(i - 1) + "-" + paintwears.get(i);
                return interVal;
            }
        }
        return "0-0.3";
    }


    /**
     * 将item转化为assets，没有磨损度的按照99999价格上架
     *
     * @return
     */
    public List<Assets> changeAssets(List<Items> items) {
        //需要上架的商品
        List<Assets> createAssets = new CopyOnWriteArrayList<>();
        for (Items item : items) {
            Assets asset = new Assets();
            asset.setAssetid(item.getAsset_info().getAssetid());
            asset.setClassid(item.getAsset_info().getClassid());
            asset.setInstanceid(item.getAsset_info().getInstanceid());
            asset.setMarket_hash_name(item.getMarket_hash_name());
            asset.setGoods_id(String.valueOf(item.getGoods_id()));
            String paintwear = item.getAsset_info().getPaintwear();
            if (StrUtil.isEmpty(paintwear)) {
                asset.setPrice("9999");
                Double income = Double.valueOf(asset.getPrice()) * 0.975;
                asset.setIncome(income.toString());
            }
            createAssets.add(asset);
        }
        return createAssets;
    }


    /**
     * buff自动逻辑
     *
     * @param items
     */
    public void manualPlus(List<Items> items) {
        //需要上架的商品
        List<Assets> createAssets = new ArrayList<>();
        //需要取消上架的，订单号
        List<String> sell_orders = new ArrayList<>();
        for (Items item : items) {
            //需要先取消上架
            if (StrUtil.isNotEmpty(item.getSell_order_id())) {
                sell_orders.add(item.getSell_order_id());
            }
            Assets asset = new Assets();
            asset.setAssetid(item.getAsset_info().getAssetid());
            asset.setClassid(item.getAsset_info().getClassid());
            //获取steam购买需要的最低销售价
            Double buySteamPrice = steamBuyItemService.getBuySteamPrice(asset.getAssetid(), asset.getClassid());
            //获取buff在销售的最低价格
            Double sellMinPrice = Double.valueOf(item.getSell_min_price());
            asset.setGoods_id(String.valueOf(item.getGoods_id()));
            asset.setMarket_hash_name(item.getMarket_hash_name());
            asset.setPrice(Double.valueOf(Math.max(buySteamPrice, sellMinPrice)).toString());
            Double income = Double.valueOf(asset.getPrice()) * 0.975;
            asset.setIncome(income.toString());
            asset.setInstanceid(item.getAsset_info().getInstanceid());
            createAssets.add(asset);
        }
        //取消上架
        cancelOrder(sell_orders);
        //进行上架操作
        sellOrderCreate(createAssets);
    }


    /**
     * 设置buff上架
     *
     * @param assets
     */

    public void sellOrderCreate(List<Assets> assets) {
        HttpHeaders headers = BuffConfig.getHeaderMap();
        headers = new HttpHeaders();
        headers.add("X-Csrftoken", BuffConfig.getCookieOnlyKey("csrf_token"));
        headers.add("Referer", "https://buff.163.com/market/steam_inventory?game=csgo");
        headers.add("Origin", "https://buff.163.com");
        headers.add("Cookie", BuffConfig.getCookie());
        ManualPlusRoot manualPlusRoot = new ManualPlusRoot();
        manualPlusRoot.setAssets(assets);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(JSONObject.parseObject(JSONObject.toJSONString(manualPlusRoot), HashMap.class), headers);
        String url = "https://buff.163.com/api/market/sell_order/create/manual_plus";
        ResponseEntity<String> responseEntity1 = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        if (responseEntity1.getStatusCode().value() != 200) {
            log.error("获取buff中可出售的商品失败，失败信息为：{}", responseEntity1.getBody());
            //获取失败
        }
        JSONObject jsonObject = JSONObject.parseObject(responseEntity1.getBody());
        if (!jsonObject.getString("code").equals("OK")) {
            //接口返回不成功
            log.error("获取buff中可出售的商品接口响应错误，错误信息为：{}", responseEntity1.getBody());
        }
        log.info("buff上架成功");
    }


    /**
     * buff上架的商品取消上架
     */
    public void cancelOrder(List<String> sell_orders) {
        String url = "https://buff.163.com/api/market/sell_order/cancel";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", BuffConfig.getCookieOnlyKey("csrf_token"));
        headers.add("Referer", "https://buff.163.com/market/sell_order/on_sale?game=csgo&mode=2,5");
        headers.add("Origin", "https://buff.163.com");
        headers.add("Cookie", BuffConfig.getCookie());
        //参数集合
        Map<String, Object> para = new HashMap();
        para.put("game", "csgo");
        para.put("sell_orders", sell_orders);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(para, headers);
        ResponseEntity<String> responseEntity1 = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        if (responseEntity1.getStatusCode().value() != 200) {
            //获取失败
        }
        JSONObject jsonObject = JSONObject.parseObject(responseEntity1.getBody());
        if (!jsonObject.getString("code").equals("OK")) {
            //接口返回不成功
        }

        log.info("buff取消上架成功");

    }
}
