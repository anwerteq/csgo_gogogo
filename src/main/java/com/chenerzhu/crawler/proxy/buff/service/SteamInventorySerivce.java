package com.chenerzhu.crawler.proxy.buff.service;


import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.entity.steamInventory.ManualPlusRoot;
import com.chenerzhu.crawler.proxy.buff.entity.steamInventory.SteamInventoryRoot;
import com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity.Items;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * buff上架service
 */
@Service
@Slf4j
public class SteamInventorySerivce {

    @Autowired
    RestTemplate restTemplate;


    /**
     * 获取buff中可交易的库存数据
     */
    public void steamInventory(){
        String url = "https://buff.163.com/api/market/steam_inventory?game=csgo&force=0&page_num=1&page_size=100&search=&state=all&_=1685501878839";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        SteamInventoryRoot steamInventoryRoot = JSONObject.parseObject(responseEntity.getBody(), SteamInventoryRoot.class);
        System.out.println("123123");
    }

    public void manualPlus(List<Items> items){
        String url = "https://buff.163.com/market/sell_order/preview/manual_plus";//post
        ManualPlusRoot manualPlusRoot = new ManualPlusRoot();
    }
}
