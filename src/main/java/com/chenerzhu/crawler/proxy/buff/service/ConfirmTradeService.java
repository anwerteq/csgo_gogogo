package com.chenerzhu.crawler.proxy.buff.service;


import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.entity.steamtradeentity.SteamTradeData;
import com.chenerzhu.crawler.proxy.buff.entity.steamtradeentity.SteamTradeRoot;
import com.chenerzhu.crawler.proxy.steam.service.GroundingService;
import com.chenerzhu.crawler.proxy.steam.service.SteamTradeofferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Set;
import java.util.stream.Collectors;


/**
 * buff确认收货service
 *
 */
@Service
@Slf4j
public class ConfirmTradeService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SteamTradeofferService steamTradeofferService;

    @Autowired
    GroundingService groundingService;

    /**
     * 获取需要确认收货的订单号
     */
    public void SteamTrade() {
        String url = "https://buff.163.com/api/market/steam_trade";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        SteamTradeRoot steamTradeRoot = JSONObject.parseObject(responseEntity.getBody(), SteamTradeRoot.class);
        if (!"OK".equals(steamTradeRoot.getCode())){
            log.error("获取确认订单数据失败");
        }
        if (steamTradeRoot.getData().isEmpty()){
            return;
        }
        Set<String> tradeIds = steamTradeRoot.getData().stream().map(SteamTradeData::getTradeofferid).collect(Collectors.toSet());
        if (tradeIds.isEmpty()){
            log.info("没有需要确认发货的信息");
            return;
        }
        try{
            steamTradeofferService.steamaccept(tradeIds);
            log.info("确认收货完成");
        }catch (Exception ex){
            log.error("确认收货失败",ex);
        }
        if (tradeIds.isEmpty()){
            return;
        }
        //steam上架商品
        groundingService.productListingOperation();
        log.info("确认收货完成和上架完成");

    }
}
