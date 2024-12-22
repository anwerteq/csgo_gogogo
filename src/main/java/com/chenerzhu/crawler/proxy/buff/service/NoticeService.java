package com.chenerzhu.crawler.proxy.buff.service;

import com.chenerzhu.crawler.proxy.applicationRunners.BuffApplicationRunner;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.buff.service.buffnotice.JsonsRootBean;
import com.chenerzhu.crawler.proxy.buff.service.buffnotice.ToDeliverOrder;
import com.chenerzhu.crawler.proxy.buff.service.deliverOrder.Data;
import com.chenerzhu.crawler.proxy.buff.service.deliverOrder.ItemsToTrade;
import com.chenerzhu.crawler.proxy.buff.service.deliverOrder.JsonsRootBeanDeliver;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.service.SteamTradeofferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.time.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * buff的相关信息
 */

@Service
@Slf4j
public class NoticeService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SteamTradeofferService steamTradeofferService;


    /**
     * 获取buff的相关通知信息
     */
    public JsonsRootBean steamTrade() {
        getNotification();
        return null;
    }

    public JsonsRootBean getNotification(){
        String url = "https://buff.163.com/api/message/notification";
        ResponseEntity<JsonsRootBean> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), JsonsRootBean.class);
        JsonsRootBean jsonsRootBean = responseEntity.getBody();
        if (!"OK".equals(jsonsRootBean.getCode())) {
            log.error("获取网易buff通知信息失败");
        }
        int csgoDeliverOrderCount = getCsgoDeliverOrderCount(jsonsRootBean);
        if (0 != csgoDeliverOrderCount) {
            requireBuyerSendOffer();
            //key：交易订单，value:商品信息
            BuffUserData buffUserData = BuffApplicationRunner.buffUserDataThreadLocal.get();
            Map<String, List<ItemsToTrade>> orderTradeofferid = getDeliverOrderTradeofferid();
            steamTradeofferService.trader(buffUserData.getSteamId(), orderTradeofferid);
            log.info("确认收货完成和上架完成");
        }
        return jsonsRootBean;
    }

    public void requireBuyerSendOffer() {
       String  url = "https://buff.163.com/api/market/steam_trade";
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        List<String> strings = exchange.getHeaders().get("set-cookie");
        String csrf_token = "";
        for (String string : strings) {
            if (string.trim().startsWith("csrf_token")){
                csrf_token = string.replace("csrf_token=", "").split(";")[0].trim();
            }
        }
        String force_buyer_send_offer = "https://buff.163.com/account/api/prefer/force_buyer_send_offer";
        Map<String, Object> data = Map.of("force_buyer_send_offer", "true");
        try {
            Map<String, String> headers = BuffConfig.getHeaderMap1();
            headers.put("X-CSRFToken",csrf_token);
            headers.put("Origin","https://buff.163.com");
            headers.put("Referer","https://buff.163.com/user-center/profile");
            HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = BuffConfig.changeBuffHttpEntity(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    force_buyer_send_offer, HttpMethod.POST,multiValueMapHttpEntity, String.class,data);
            if (response.getStatusCode() == HttpStatus.OK) {
                String body = response.getBody();
                if (body != null && body.contains("\"code\":\"OK\"")) {
                    log.info("已开启买家发起交易报价功能");
                } else {
                    log.error("开启买家发起交易报价功能失败");
                }
            } else {
                log.error("开启买家发起交易报价功能失败: HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("开启买家发起交易报价功能失败", e);
        }
    }


    /**
     * 获取待处理的csgo订单
     *
     * @return
     */
    public int getCsgoDeliverOrderCount(JsonsRootBean jsonsRootBean) {
        ToDeliverOrder toDeliverOrder = jsonsRootBean.getData().getToDeliverOrder();
        log.info("buff待处理的csgo订单数量为：{}", toDeliverOrder.getCsgo());
        return toDeliverOrder.getCsgo();
    }


    /**
     * 获取buff待处理订单信息
     *
     * @return
     */
    private JsonsRootBeanDeliver getDeliverOrder() {
        String url = "https://buff.163.com/api/market/steam_trade";
        ResponseEntity<JsonsRootBeanDeliver> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), JsonsRootBeanDeliver.class);
        JsonsRootBeanDeliver body = responseEntity.getBody();
        return body;
    }

    /**
     * 获取steam待确认的交易订单id集合
     *
     * @return
     */
    public Map<String, List<ItemsToTrade>> getDeliverOrderTradeofferid() {
        JsonsRootBeanDeliver deliverOrder = getDeliverOrder();
        List<Data> datas = deliverOrder.getData();
        Map<String, List<ItemsToTrade>> tradeofferidMap = datas.stream().collect(Collectors.toMap(Data::getTradeofferid, Data::getItemsToTrade));
        return tradeofferidMap;
    }
}
