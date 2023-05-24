package com.chenerzhu.crawler.proxy.pool.csgo.service;


import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity.*;
import com.chenerzhu.crawler.proxy.pool.csgo.buyentity.PayBillRepData;
import com.chenerzhu.crawler.proxy.pool.csgo.buyentity.PayBillRepRoot;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffCreateBillRoot;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellSteamProfitEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.SellSteamProfitRepository;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * buff购买商品服务
 */
@Service
@Slf4j
public class BuffBuyItemService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SellSteamProfitRepository sellSteamProfitRepository;


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
        log.info("商品id:" + goods_id);
        ResponseEntity<BuffCreateBillRoot> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), BuffCreateBillRoot.class);
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
    public PayBillRepData payBill(String sell_order_id, int goods_id, String price) {
        HttpHeaders headers1 = new HttpHeaders() {{
            //buff支付订单添加请求头
            set("Referer", "https://buff.163.com/goods/903822?from=market");
        }};
        headers1.add("Cookie", BuffConfig.buffCookie);
        for (String one : BuffConfig.buffCookie.split(";")) {
            if ("csrf_token".equals(one.split("=")[0].trim())) {
                headers1.add("X-CSRFToken", one.split("=")[1].trim());
            }
        }

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
        String url = "https://buff.163.com/api/market/goods/buy";
        ResponseEntity<String> responseEntity1 = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class);
        if (responseEntity1.getStatusCode().value() != 200) {
            throw new ArithmeticException("支付接口调用失败");
        }
        log.info("buff订单支付成功");
        String body = responseEntity1.getBody();
        PayBillRepRoot payBillRepRoot = JSONObject.parseObject(body, PayBillRepRoot.class);
        return payBillRepRoot.getData();
    }


    /**
     * 购买buff商品的逻辑
     *
     * @param goods_id:商品id
     */
    public void buffSellOrder(String goods_id, int num) {

        List<SellSteamProfitEntity> select = sellSteamProfitRepository.selectOrderAsc();
        Collections.shuffle(select);
        for (SellSteamProfitEntity entity : select) {
            goods_id = String.valueOf(entity.getItem_id());
            //获取该商品售卖的列表信息
            String url = "https://buff.163.com/api/market/goods/sell_order?game=csgo&goods_id=" + goods_id + "" +
                    "&sort_by=default&mode=&allow_tradable_cooldown=1&_=" + System.currentTimeMillis() + "&page_num= " + 1;
            ResponseEntity<BuffBuyRoot> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), BuffBuyRoot.class);
            if (responseEntity.getStatusCode().value() != 200) {
                throw new ArithmeticException("查询接口调用失败");
            }
            BuffBuyRoot body = responseEntity.getBody();
            if (!"OK".equals(body.getCode())) {
                throw new ArithmeticException("查询接口调用异常");
            }
            num = 1;
            for (BuffBuyItems buyItems : responseEntity.getBody().getData().getItems()) {
                //单件商品大于10的 跳过
                if (Double.parseDouble(buyItems.getPrice()) >= 10) {
                    break;
                }
                //不支持支付宝跳过
                if (!buyItems.getSupported_pay_methods().contains(3)) {
                    continue;
                }
                num--;
                //创建订单
                createBill(buyItems.getId(), buyItems.getGoods_id(), buyItems.getPrice());
                //支付订单
                PayBillRepData payBillRepData = payBill(buyItems.getId(), buyItems.getGoods_id(), buyItems.getPrice());

                //卖家报价
                askSellerToSendOffer(payBillRepData.getId(),String.valueOf(buyItems.getGoods_id()));
                if (num <= 0) {
                    log.info("商品购买完成");
                    break;
                }

                // 230524T0364404819
            }
        }

    }

    /**
     * 购买buff东西.让卖家报价
     */
    public void askSellerToSendOffer(String bill_orderId, String goodsId) {
        String url = "https://buff.163.com/api/market/bill_order/ask_seller_to_send_offer";
        String referer = "https://buff.163.com/goods/#?from=market".replace("#", goodsId);
        HttpHeaders headers1 = new HttpHeaders() {{
            //buff支付订单添加请求头
            set("Referer", referer);
        }};
        headers1.add("Cookie", BuffConfig.buffCookie);
        for (String one : BuffConfig.buffCookie.split(";")) {
            if ("csrf_token".equals(one.split("=")[0].trim())) {
                headers1.add("X-CSRFToken", one.split("=")[1].trim());
            }
        }
        HashMap<String, Object> whereMap = new HashMap();
        List<String> bill_orders = new ArrayList() {{
            add(bill_orderId);// 230524T0364391346
        }};
        whereMap.put("bill_orders", bill_orders);
        whereMap.put("game", "csgo");
        System.out.println(JSONObject.toJSONString(whereMap));
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity(whereMap, headers1);
        BuffConfig.syncCookie();
        ResponseEntity<String> responseEntity1 = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class);
        if (responseEntity1.getStatusCode().value()  != 200){
            log.error("让卖家发送报价失败");
        }
        log.info("让卖家报价返回的数据：" + responseEntity1.getBody());
    }

    /**
     * 主动报价：确定交易
     */
    public void confirmDending() {
        String url = "https://buff.163.com/api/message/notification?_=" + System.currentTimeMillis();

        while (true) {
            ResponseEntity<ConfirmDendingRoot> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), ConfirmDendingRoot.class);
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
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
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
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        System.out.println("getToDeliver:" + responseEntity.getBody());

    }


    public void getBuyOrder() {
        String url = "https://buff.163.com/api/market/buy_order/history?page_num=1&page_size=200&state=trading&game=csgo&appid=730";
        ResponseEntity<BuyOrderHistoryRoot> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), BuyOrderHistoryRoot.class);
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

}
