package com.chenerzhu.crawler.proxy.buff.service;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.ExecutorUtil;
import com.chenerzhu.crawler.proxy.buff.entity.steamInventory.ManualPlusRoot;
import com.chenerzhu.crawler.proxy.buff.entity.steamInventory.SteamInventoryRoot;
import com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity.Items;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.Assets;
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
     * 获取buff中可交易的库存数据
     */
    public void steamInventory() {
        //查询的为可交易的
        String url = "https://buff.163.com/api/market/steam_inventory?game=csgo&force=0&page_num=1&page_size=50&search=&sort_by=time.desc&state=tradable";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        SteamInventoryRoot steamInventoryRoot = JSONObject.parseObject(responseEntity.getBody(), SteamInventoryRoot.class);
        manualPlus(steamInventoryRoot.getData().getItems());
        System.out.println("123123");
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
            asset.setPrice(Double.valueOf(Math.max(buySteamPrice,sellMinPrice)).toString());
            Double income = Double.valueOf(asset.getPrice()) * 0.975;
            asset.setIncome(income.toString());
            asset.setInstanceid(item.getAsset_info().getInstanceid());
            createAssets.add(asset);
        }
        //取消上架
        cancelOrder(sell_orders);
        //进行上架操作
        sellOrderCreate(createAssets);
        ExecutorUtil.pool.execute(()->steamBuyItemService.afterTopBuffUpdateCost(createAssets));
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
            log.error("获取buff中可出售的商品失败，失败信息为：{}",responseEntity1.getBody());
            //获取失败
        }
        JSONObject jsonObject = JSONObject.parseObject(responseEntity1.getBody());
        if (!jsonObject.getString("code").equals("OK")) {
            //接口返回不成功
            log.error("获取buff中可出售的商品接口响应错误，错误信息为：{}",responseEntity1.getBody());
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
