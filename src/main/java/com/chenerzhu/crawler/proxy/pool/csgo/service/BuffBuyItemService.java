package com.chenerzhu.crawler.proxy.pool.csgo.service;


import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.config.ApplicationContextConfig;
import com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity.*;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffCreateBillRoot;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffPayBillRoot;
import com.chenerzhu.crawler.proxy.pool.util.HttpsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sun.net.www.protocol.https.Handler;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

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
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
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
    public void getSteamInventory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpHeaders headers1 = new HttpHeaders() {{
            //buff支付订单添加请求头
            set("Host", "steamcommunity.com");
            set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/113.0");
            set("Accept", "*/*");
            set("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            set("Accept-Encoding", "gzip, deflate, br");
            set("X-Requested-With", "XMLHttpRequest");
            set("Connection", "keep-alive");
            set("Referer", "https://steamcommunity.com/profiles/76561199351185401/inventory?modal=1&market=1");
            set("Cookie", " timezoneOffset=28800,0; _ga=GA1.2.1888222838.1684654738; _gid=GA1.2.1146899614.1684654738; steamLoginSecure=76561199351185401%7C%7CeyAidHlwIjogIkpXVCIsICJhbGciOiAiRWREU0EiIH0.eyAiaXNzIjogInI6MEQyRV8yMjhEQTdBN183REE2NiIsICJzdWIiOiAiNzY1NjExOTkzNTExODU0MDEiLCAiYXVkIjogWyAid2ViIiBdLCAiZXhwIjogMTY4NDc0MTYzMywgIm5iZiI6IDE2NzYwMTQ4MjMsICJpYXQiOiAxNjg0NjU0ODIzLCAianRpIjogIjEyMTNfMjI4RUVCQTJfNjQwNDIiLCAib2F0IjogMTY4NDY1NDgyMSwgInJ0X2V4cCI6IDE3MDI1OTIzNzksICJwZXIiOiAwLCAiaXBfc3ViamVjdCI6ICIxNjMuMTIzLjE5Mi40NSIsICJpcF9jb25maXJtZXIiOiAiMTYzLjEyMy4xOTIuNDUiIH0.46epJ50pEEs-kdMz5KcfTvJp8lCOSyVUhCz_j5sPaBM-CFledrIWQXZsRK7kohDy43uRWfiiuyEN3F_aRnwmBQ; browserid=2911059127808429915; sessionid=b96bccb6a6416ad19df2e8f6; webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22new_device_cooldown_days%22%3A0%2C%22time_checked%22%3A1684730443%7D; strInventoryLastContext=730_2");
            set("Sec-Fetch-Dest", "empty");
            set("Sec-Fetch-Mode", "cors");
            set("Sec-Fetch-Site", "same-origin");
            set("TE", "trailers");
            set("Content-Type", "application/json");
        }};
        validateHttps();
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity(headers1);
        String url = "https://steamcommunity.com/inventory/76561199351185401/730/2?l=schinese&count=75&market=1";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity1, String.class);
        System.out.println("123123112");

    }


    public static boolean validateHttps() {
        String url1 = "https://steamcommunity.com/inventory/76561199351185401/730/2?l=schinese&count=75&market=1";
        boolean available = false;
        HttpsURLConnection httpsURLConnection = null;
        try {
            URL url = new URL(null, url1, new Handler());
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setSSLSocketFactory(HttpsUtils.getSslSocketFactory());
            httpsURLConnection.setHostnameVerifier(HttpsUtils.getTrustAnyHostnameVerifier());
            httpsURLConnection.setRequestProperty("accept", "");
            httpsURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpsURLConnection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
            httpsURLConnection.setConnectTimeout(2 * 1000);
            httpsURLConnection.setReadTimeout(3 * 1000);
            httpsURLConnection.setInstanceFollowRedirects(false);



            BufferedReader br = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            String s = null;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            if (httpsURLConnection.getResponseCode() == 200) {
                available = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            available = false;
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        }
        return available;
    }

}
