package com.chenerzhu.crawler.proxy.pool.csgo.service;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity.*;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffCreateBillRoot;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffPayBillRoot;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.Assets;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.Descriptions;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.InventoryRootBean;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.PriceVerviewRoot;
import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chenerzhu.crawler.proxy.pool.csgo.service.ItemGoodsService.getHttpEntity;
import static com.chenerzhu.crawler.proxy.pool.csgo.service.ItemGoodsService.syncCookie;

/**
 * buff购买商品服务
 */
@Service
@Slf4j
public class BuffBuyItemService {

    @Autowired
    RestTemplate restTemplate;


    /**
     * 在buff购买下订单
     *
     * @param sell_order_id：销售订单
     * @param goods_id：商品id
     * @param price:销售价格         //allow_tradable_cooldown：是否可以否定（0：是，1：否）,cdkey_id： _:时间戳
     */
    public void createBill(String sell_order_id, int goods_id, String price) {
        //get请求
        String url = "https://buff.163.com/api/market/goods/buy/preview?game=csgo&sell_order_id=" + sell_order_id + "&" +
                "goods_id=" + goods_id + "&price=" + price + "&allow_tradable_cooldown=0&cdkey_id=&_=" + System.currentTimeMillis();
        ResponseEntity<BuffCreateBillRoot> responseEntity = restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), BuffCreateBillRoot.class);
        if (responseEntity.getStatusCode().value() != 200) {
            throw new ArithmeticException("创建订单接口调用失败");
        }
        BuffCreateBillRoot body = responseEntity.getBody();
        if (!"OK".equals(body.getCode())) {
            throw new ArithmeticException("创建订单异常");
        }
        log.info("buff订单创建成功");
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }


    /**
     * buff购买支付订单   post请求 pay_method:3(支付宝)
     * //参数： {"game":"csgo","goods_id":903832,"sell_order_id":"230521T0369835303","price":0.86,"pay_method":3,"allow_tradable_cooldown":0,"token":"","cdkey_id":""}
     */
    public void payBill(String sell_order_id, int goods_id, String price) {
        HttpHeaders headers1 = new HttpHeaders() {{
            //buff支付订单添加请求头
            set("Host", " buff.163.com");
            set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/111.0");
            set("Accept", " application/json, text/javascript, */*; q=0.01");
            set("Accept-Language", " zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            set("Accept-Encoding", " gzip, deflate, br");
            set("Content-Type", " application/json");
            set("X-CSRFToken", " IjcxMTBkMzY5ZDhjMGU3YWMyZjI1MDRmZGExZTZlYWQ5NWFiOTdlZmIi.F0ti0A.OBh4vnjjjSasyB2YCkuScPtAzJQ");
            set("X-Requested-With", " XMLHttpRequest");
            set("Connection", " keep-alive");
            set("Cookie", " csrf_token=IjcxMTBkMzY5ZDhjMGU3YWMyZjI1MDRmZGExZTZlYWQ5NWFiOTdlZmIi.F0ti0A.OBh4vnjjjSasyB2YCkuScPtAzJQ; Device-Id=gWuF5A6BKxVnJNmFrnkt; Locale-Supported=zh-Hans; game=csgo; session=1-WfiWDKomdnq5YqFXQ5OaffzeQqM6mSHuM4XyYLGNmZXn2030407391");
            set("Sec-Fetch-Dest", " empty");
            set("Sec-Fetch-Mode", " cors");
            set("Sec-Fetch-Site", " same-origin");
            set("Referer", "https://buff.163.com/goods/903822?from=market");
            ;
            set("Origin", "https://buff.163.com");
            ;
        }};
        HashMap<String, Object> whereMap = new HashMap();
        whereMap.put("game", "csgo");
        whereMap.put("goods_id", goods_id);
        whereMap.put("sell_order_id", sell_order_id);
        whereMap.put("price", Double.parseDouble(price));
        whereMap.put("pay_method", 3);
        whereMap.put("allow_tradable_cooldown", 0);
        whereMap.put("token", "");
        whereMap.put("cdkey_id", "");
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity(whereMap, headers1);
        syncCookie();

        System.out.println(JSONObject.toJSONString(whereMap));
        String url = "https://buff.163.com/api/market/goods/buy";
        ResponseEntity<String> responseEntity1 = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class);
        BuffPayBillRoot responseEntity = new BuffPayBillRoot();
        if (responseEntity1.getStatusCode().value() != 200) {
            throw new ArithmeticException("支付接口调用失败");
        }
//        BuffPayBillRoot body = responseEntity.get();
//        if (!"OK".equals(body.getCode())) {
//            throw new ArithmeticException("支付接口调用异常");
//        }
        log.info("buff订单支付成功");
    }


    /**
     * 购买buff商品的逻辑
     *
     * @param goods_id:商品id
     */
    public void buffSellOrder(String goods_id, int num) {
        String url = "https://buff.163.com/api/market/goods/sell_order?game=csgo&goods_id=" + goods_id + "" +
                "&sort_by=default&mode=&allow_tradable_cooldown=1&_=" + System.currentTimeMillis() + "&page_num= " + 1;
        ResponseEntity<BuffBuyRoot> responseEntity = restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), BuffBuyRoot.class);
        if (responseEntity.getStatusCode().value() != 200) {
            throw new ArithmeticException("查询接口调用失败");
        }
        BuffBuyRoot body = responseEntity.getBody();
        if (!"OK".equals(body.getCode())) {
            throw new ArithmeticException("查询接口调用异常");
        }
        for (BuffBuyItems item : responseEntity.getBody().getData().getItems()) {
            num--;
            //创建订单
            createBill(item.getId(), item.getGoods_id(), item.getPrice());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //支付订单
            payBill(item.getId(), item.getGoods_id(), item.getPrice());
            if (num <= 0) {
                log.info("商品购买完成");
                return;
            }
        }
    }


    /**
     * 主动报价：确定交易
     */
    public void confirmDending() {
        String url = "https://buff.163.com/api/message/notification?_=" + System.currentTimeMillis();

        while (true) {
            ResponseEntity<ConfirmDendingRoot> responseEntity = restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), ConfirmDendingRoot.class);
            int csgo = responseEntity.getBody().getData().getTo_deliver_order().getCsgo();
            if (csgo != 0) {
//   // https://buff.163.com/api/market/goods/recommendation
            }
            System.out.println("responseEntity:" + responseEntity);
            System.out.println("123123");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getSteamTrade();
        }

    }


    public void getSteamTrade() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String url = "https://buff.163.com/api/market/steam_trade";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), String.class);
        System.out.println("getSteamTrade:" + responseEntity.getBody());
        System.out.println("123123");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getToDeliver();

    }

    /**
     * 获取执行数据
     */
    public void getToDeliver() {
        String url = "https://buff.163.com/api/market/sell_order/to_deliver?game=csgo&appid=730";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), String.class);
        System.out.println("getToDeliver:" + responseEntity.getBody());

    }


    public void getBuyOrder() {
        String url = "https://buff.163.com/api/market/buy_order/history?page_num=1&page_size=200&state=trading&game=csgo&appid=730";
        ResponseEntity<BuyOrderHistoryRoot> responseEntity = restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), BuyOrderHistoryRoot.class);
        List<BuyOrderHistoryData> dataList = responseEntity.getBody().getData();
        if (dataList.isEmpty()) {
            //没有需要确认发货的数据
            return;
        }
        //确认发货需要确认金额是否合理

        for (BuyOrderHistoryData historyData : dataList) {

        }

        System.out.println("123123");
    }

    /**
     * 获取steam库存
     */
    public String getSteamInventory() {
        String url = "https://steamcommunity.com/inventory/76561199351185401/730/2?l=schinese&count=75&market=1";

        String resStr = HttpClientUtils.sendGet(url, getSteamHeader());
        if (StrUtil.isEmpty(resStr)) {
            log.error("获取steam库存失败");
        }
        InventoryRootBean inventoryRootBean = JSONObject.parseObject(resStr, InventoryRootBean.class);
        for (Descriptions description : inventoryRootBean.getDescriptions()) {
            //价格数据
            PriceVerviewRoot priceVerview = getPriceVerview(description.getMarket_hash_name());
            //获取 assetid
            String assetid = "";
            for (Assets asset : inventoryRootBean.getAssets()) {
                if (asset.getClassid() == description.getClassid()) {
                    assetid = asset.getAssetid();
                }
            }
            saleItem(assetid, priceVerview.getLowest_price());
            log.info("steam成功上架商品名称：" + description.getName());
        }
        return "123";

    }

    /**
     * 获取商品的参考价格
     *
     * @param market_hash_name
     * @return
     */
    public PriceVerviewRoot getPriceVerview(String market_hash_name) {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String url = null;
        try {
            url = "https://steamcommunity.com/market/priceoverview/?country=US&currency=1&appid=730&market_hash_name=" + URLEncoder.encode(market_hash_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String resStr = HttpClientUtils.sendGet(url, getSteamHeader());
        if (StrUtil.isEmpty(resStr)) {
            log.error("获取参数的参考价格失败");
            throw new ArithmeticException("获取参数的参考价格失败");
        }
        PriceVerviewRoot priceVerviewRoot = JSONObject.parseObject(resStr, PriceVerviewRoot.class);
        return priceVerviewRoot;
    }

    /**
     * 销售商品
     *
     * @param assetid
     * @param price
     */
    public void saleItem(String assetid, String price) {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String url = "https://steamcommunity.com/market/sellitem/";
        Map<String, String> paramerMap = new HashMap<>();
        paramerMap.put("sessionid", "4442a1bda6b1feee2ca2727f");
        paramerMap.put("appid", "730");
        paramerMap.put("contextid", "2");
        paramerMap.put("assetid", assetid);
        paramerMap.put("price", price);
        String responseStr = HttpClientUtils.sendPostForm(url, "", getSteamHeader(), paramerMap);
        System.out.println("1231231");
    }


    /**
     * steam请求头
     *
     * @return
     */
    public static Map<String, String> getSteamHeader() {
        Map<String, String> headers1 = new HashMap() {{
            //buff支付订单添加请求头
            put("Host", "steamcommunity.com");
            put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/113.0");
            put("Accept", "*/*");
            put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            put("Accept-Encoding", "gzip, deflate, br");
            put("X-Requested-With", "XMLHttpRequest");
            put("Connection", "keep-alive");
            put("Referer", "https://steamcommunity.com/profiles/76561199351185401/inventory?modal=1&market=1");
            put("Cookie", "timezoneOffset=28800,0; browserid=2911058493333424353; sessionid=4442a1bda6b1feee2ca2727f; steamCountry=SG%7Cc4c23dc904408d47f98262d48bc48452; steamLoginSecure=76561199351185401%7C%7CeyAidHlwIjogIkpXVCIsICJhbGciOiAiRWREU0EiIH0.eyAiaXNzIjogInI6MEQyRl8yMjhEQTdDRV9GQ0M4NCIsICJzdWIiOiAiNzY1NjExOTkzNTExODU0MDEiLCAiYXVkIjogWyAid2ViIiBdLCAiZXhwIjogMTY4NDgxMjM2NSwgIm5iZiI6IDE2NzYwODUwMTEsICJpYXQiOiAxNjg0NzI1MDExLCAianRpIjogIjEyMTNfMjI4RUVCQ0FfOTU3NTMiLCAib2F0IjogMTY4NDcyNTAwOSwgInJ0X2V4cCI6IDE3MDI5MjkwMjEsICJwZXIiOiAwLCAiaXBfc3ViamVjdCI6ICIzLjEuODUuMjA4IiwgImlwX2NvbmZpcm1lciI6ICIzLjEuODUuMjA4IiB9.emm9iOPWnMDu8B-5Jrza15iTMmPjw44cwztnJ4R6l7Z7dfyc8tCjc85HKcWyOdjq0EONPYM49U-BjqeMkqqVDg; webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22new_device_cooldown_days%22%3A0%2C%22time_checked%22%3A1684728034%7D; strInventoryLastContext=730_2");
            put("Sec-Fetch-Dest", "empty");
            put("Sec-Fetch-Mode", "cors");
            put("Sec-Fetch-Site", "same-origin");
            put("TE", "trailers");
            put("Content-Type", "application/json; charput=ISO-8859-1");
        }};
        return headers1;
    }
}
