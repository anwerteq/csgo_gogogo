package com.chenerzhu.crawler.proxy.buff.service;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.applicationRunners.BuffApplicationRunner;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.buff.entity.steamInventory.ManualPlusRoot;
import com.chenerzhu.crawler.proxy.buff.entity.steamInventory.SteamInventoryData;
import com.chenerzhu.crawler.proxy.buff.entity.steamInventory.SteamInventoryRoot;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.BuffBuyData;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.BuffBuyItems;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.BuffBuyRoot;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.Items;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Assets;
import com.chenerzhu.crawler.proxy.steam.entity.Descriptions;
import com.chenerzhu.crawler.proxy.steam.repository.DescriptionsRepository;
import com.chenerzhu.crawler.proxy.steam.service.SteamBuyItemService;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.CheckWearUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
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

    public static String priceMax = "49.69";

    @Value("${buff_user_id}")
    private String buffUserIds;

    @Autowired
    DescriptionsRepository descriptionsRepository;


    /**
     * 获取buff中可出售的库存数据
     */
    public List<Items> steamInventory(int page_num) {
        //查询的为可交易的
        String url = "https://buff.163.com/api/market/steam_inventory?game=csgo&force=1&page_num=1&page_size=500&search=&state=cansell&_=" + System.currentTimeMillis()+"&page_num="+page_num;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        SteamInventoryRoot steamInventoryRoot = JSONObject.parseObject(responseEntity.getBody(), SteamInventoryRoot.class);
        List<Items> items = steamInventoryRoot.getData().getItems();
        return items;
    }

    /**
     * 获取所有状态的数据
     *
     * @param page_num
     * @return
     */
    public List<Items> steamAllStatusInventory(int page_num) {
        log.info("开始拉取,buff库存的第:{} 页", page_num);
        SleepUtil.sleep(5 * 1040);
        //查询的为可交易的
        String url = "https://buff.163.com/api/market/steam_inventory?game=csgo&page_num=" + page_num + "&page_size=500&search=&_=" + System.currentTimeMillis();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        SteamInventoryRoot steamInventoryRoot = JSONObject.parseObject(responseEntity.getBody(), SteamInventoryRoot.class);
        SteamInventoryData data = steamInventoryRoot.getData();
        List<Items> items = data.getItems();
        int start = (page_num - 1) * 500;
        int total_count = data.getTotal_count();
        if (start > total_count) {
            return new ArrayList<>();
        }
        return items;
    }

    /**
     * 自动上架逻辑
     */
    public Boolean autoSale(int page_num) {
        log.info("拉取buff，第{}页库存信息，", page_num);
        List<Items> items = steamInventory(page_num);
        BuffUserData buffUserData = BuffApplicationRunner.buffUserDataThreadLocal.get();
        if (items.isEmpty()) {
            log.info("buff账号:{},库存中没有可上架饰品", buffUserData.getAcount());
            return false;
        }
        //获取steam库存信息
        List<Descriptions> allBySteamId = descriptionsRepository.findAllBySteamId(buffUserData.getSteamId());
        if (allBySteamId.isEmpty()) {
            throw new RuntimeException("buff账号:"+buffUserData.getAcount() +" 未更新库存");
        }
        Map<String, Double> cdKeyIdAndPrice = allBySteamId.stream().filter(o->o.getBuy_price() != null).collect(Collectors.toMap(Descriptions::getCdkey_id, Descriptions::getBuy_price));
        List<Assets> assets = new ArrayList<>();
        int count = 0;
        for (Items item : items) {
            Double sellMinPrice = Double.valueOf(item.getSell_min_price()) ;
            //限制售卖的价格
           // 没有完全刷新库存信息
            Double buyPrice = cdKeyIdAndPrice.get(item.getAssetidClassidInstanceid());
            if (buyPrice == null){
                continue;
            }
            //低于成本，不售卖
            if (sellMinPrice <= buyPrice * 6){
                continue;
            }
            Double realtimeSellPrice = getSellPrice(String.valueOf(item.getGoods_id()));
            //低于成本，不售卖
            if (realtimeSellPrice <= buyPrice * 6){
                continue;
            }
            Assets asset = buildSell_orderParam(item, realtimeSellPrice);
            count++;
            log.info("饰品:{}准备上架数据中,在售价格:{},本价格：{}元", asset.getMarket_hash_name(), asset.getPrice(), buyPrice * 6);
            assets.add(asset);
            if (count > 40) {
                if ( !sellOrderCreate(assets)){
                    return false;
                }
                log.info("buff账号:{},一共上架商品数量为:{},休眠30s", buffUserData.getAcount(), assets.size());
                SleepUtil.sleep(30 * 1000);
                assets.clear();
                count= 0;
            }

        }
        if ( !sellOrderCreate(assets)){
            return false;
        }
        log.info("buff账号:{},一共上架商品数量为:{},休眠30s", buffUserData.getAcount(), assets.size());
        assets.clear();
        return true;
    }


    public Assets buildSell_orderParam(Items item, Double realtimeSellPrice){
        Assets asset = new Assets();
        asset.setAssetid(item.getAsset_info().getAssetid());
        asset.setClassid(item.getAsset_info().getClassid());
        asset.setInstanceid(item.getAsset_info().getInstanceid());
        asset.setMarket_hash_name(item.getMarket_hash_name());
        asset.setGoods_id(String.valueOf(item.getGoods_id()));
        asset.setPrice(String.valueOf(realtimeSellPrice));
        asset.setIncome(String.valueOf(realtimeSellPrice * 0.975));
        return asset;
    }
    public static void main(String[] args) {

    }



    /**
     * 根据磨损度获取售卖列表的价格
     *
     * @return
     */
    public Double getSellPrice(String goods_id) {
        SleepUtil.sleep(5500);
        String url = "https://buff.163.com/api/market/goods/sell_order?game=csgo&goods_id=" + goods_id
                + "&page_num=1&sort_by=default&mode=&allow_tradable_cooldown=1&min_paintwear="
                + "&_=" + System.currentTimeMillis();
        ResponseEntity<BuffBuyRoot> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), BuffBuyRoot.class);
        BuffBuyRoot body = responseEntity.getBody();
        BuffBuyData data = body.getData();
        List<BuffBuyItems> items = data.getItems();
        BuffBuyItems buffBuyItems = items.get(1);
        Double price = Double.valueOf(buffBuyItems.getPrice());

        return price;
    }


    /**
     * 根据磨损度获取售卖列表的价格
     *
     * @return
     */
    public String getSellPrices(String goods_id, String paintwearInterval) {
        String min_paintwear = paintwearInterval.split("-")[0];
        String max_paintwear = paintwearInterval.split("-")[1];
        String url = "https://buff.163.com/api/market/goods/sell_order?game=csgo&goods_id=" + goods_id
                + "&page_num=1&sort_by=default&mode=&allow_tradable_cooldown=1&min_paintwear="
                + min_paintwear + "&max_paintwear=" + max_paintwear + "&_=" + System.currentTimeMillis();
        ResponseEntity<BuffBuyRoot> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), BuffBuyRoot.class);
        BuffBuyRoot body = responseEntity.getBody();
        BuffBuyData data = body.getData();
        List<BuffBuyItems> items = data.getItems();
        SleepUtil.sleep(5500);
        Double sumPrice = 0.0;
        items = items.subList(0, Math.min(10, items.size()));
        for (BuffBuyItems item : items) {
            sumPrice = sumPrice + Double.valueOf(item.getPrice());
        }

        String price = String.valueOf(sumPrice / items.size());
        return price;
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

                Double income = Double.valueOf(asset.getPrice()) * 0.975;
                asset.setIncome(income.toString());
            }
            createAssets.add(asset);
        }
        return createAssets;
    }

    /**
     * 获取在售商品信息订单id集合
     */
    public Set<String> getOnSale(int count) {
        String url = "https://buff.163.com/api/market/sell_order/on_sale?page_num=1&sort_by=updated.asc" +
                "&mode=2%2C5&game=csgo&appid=730&page_size=40&min_price=" + priceMax + "&max_price=" + priceMax;
        String responseStr = HttpClientUtils.sendGet(url, BuffConfig.getHeaderMap1());
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        Object codeObj = jsonObject.get("code");
        if (ObjectUtil.isNull(codeObj) || !"OK".equals(codeObj.toString())) {
            BuffUserData buffUserData = BuffApplicationRunner.buffUserDataThreadLocal.get();
            log.error("buff账号:{},获取库存信息异常：{}", buffUserData.getAcount(), jsonObject);
        }
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray items = data.getJSONArray("items");
        Set<String> idSet = new HashSet();
        for (Object item : items) {
            JSONObject jobj = (JSONObject) item;
            Object id = jobj.get("id");
            if (ObjectUtil.isNotNull(id)) {
                idSet.add(id.toString());
            }
        }
        return idSet;
    }

    /**
     * 下架在售商品
     */
    public void downOnSale() {
        Set<String> onSale = getOnSale(0);
        if (onSale.isEmpty()) {
            return;
        }
        onSale = onSale.stream().limit(Long.valueOf("40")).collect(Collectors.toSet());
        //取消上架
        cancelOrder(onSale);
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
//        sellOrderCreate(createAssets);
    }


    /**
     * 设置buff上架
     *
     * @param assets
     */

    public Boolean sellOrderCreate(List<Assets> assets) {
        if (assets.isEmpty()){
            return false;
        }
        BuffUserData buffUserData = BuffApplicationRunner.buffUserDataThreadLocal.get();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", BuffConfig.getCookieOnlyKey("csrf_token"));
        headers.add("Referer", "https://buff.163.com/market/steam_inventory?game=csgo");
        headers.add("Origin", "https://buff.163.com");
        headers.add("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        headers.add("Cookie", BuffConfig.getCookie());
        ManualPlusRoot manualPlusRoot = new ManualPlusRoot();
        manualPlusRoot.setAssets(assets);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(JSONObject.parseObject(JSONObject.toJSONString(manualPlusRoot), HashMap.class), headers);
        String url = "https://buff.163.com/api/market/sell_order/create/manual_plus";
        ResponseEntity<String> responseEntity1 = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        if (responseEntity1.getStatusCode().value() != 200) {
            log.error("buff账号:{},获取可出售的商品失败，失败信息为：{}", buffUserData.getAcount(), responseEntity1.getBody());
            return true;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseEntity1.getBody());
        if (!"OK".equals(jsonObject.getString("code"))) {
            //接口返回不成功
            log.error("buff账号:{},获取buff中可出售的商品接口响应错误，错误信息为：{}", buffUserData.getAcount(), responseEntity1.getBody());
            SleepUtil.sleep(20 * 1000);
            return true;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (ObjectUtil.isNull(data)) {
            log.error("buff账号:{},获取buff中可出售的商品数据错误，错误信息为：{}", buffUserData.getAcount(), responseEntity1.getBody());
            return true;
        }
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            if (value.toString().contains("你已达到上架数量上限")) {
                log.error("buff账号:{},货架已满给，切换下一个账号", buffUserData.getAcount());
                return false;
            } else if (value.toString().contains("Ok")) {
            } else {

            }
            int a = 0;
        }
        log.info("buff上架成功");
        return true;
    }


    /**
     * buff上架的商品取消上架
     */
    public void cancelOrder(Collection<String> sell_orders) {
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
}
