package com.chenerzhu.crawler.proxy.buff.service;


import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.entity.steamInventory.ManualPlusRoot;
import com.chenerzhu.crawler.proxy.buff.entity.steamInventory.SteamInventoryRoot;
import com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity.Items;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.Assets;
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
import java.util.List;
import java.util.stream.Collectors;

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
        //查询的为可交易的
        String url = "https://buff.163.com/api/market/steam_inventory?game=csgo&force=0&page_num=1&page_size=50&search=&sort_by=time.desc&state=tradable";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        SteamInventoryRoot steamInventoryRoot = JSONObject.parseObject(responseEntity.getBody(), SteamInventoryRoot.class);
        manualPlus(steamInventoryRoot.getData().getItems());
        System.out.println("123123");
    }


    /**
     * buff自动逻辑
     * @param items
     */
    public void manualPlus(List<Items> items){
        List<Assets> assets = new ArrayList<>();
        for (Items item : items) {
            Assets asset = new Assets();
            asset.setAssetid(item.getAsset_info().getAssetid());
            asset.setClassid(item.getAsset_info().getClassid());
            asset.setGoods_id(String.valueOf(item.getGoods_id()));
            asset.setMarket_hash_name(item.getMarket_hash_name());
            asset.setPrice(item.getSell_min_price());
            Double income = Double.valueOf(asset.getPrice()) * 0.975;
            asset.setIncome(income.toString());
            assets.add(asset);
        }
        sellOrderCreate(assets);
//        String url = "https://buff.163.com/market/sell_order/preview/manual_plus";//post
//        HttpHeaders headers = BuffConfig.getHeaderMap();
//        headers.add("X-Csrftoken",  BuffConfig.getCookieOnlyKey("csrf_token"));
//        headers.add("Referer",  "https://buff.163.com/market/steam_inventory?game=csgo");
//        headers.add("Origin",  "https://buff.163.com");
//        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(new ManualPlusRoot(), headers);
//        ResponseEntity<String> responseEntity1 = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
//        ManualPlusRoot manualPlusRoot = new ManualPlusRoot();
    }


    public void sellOrderCreate(List<Assets> assets){
        HttpHeaders headers = BuffConfig.getHeaderMap();
        headers.add("X-Csrftoken", BuffConfig.getCookieOnlyKey("csrf_token"));
        headers.add("Referer", "https://buff.163.com/market/steam_inventory?game=csgo");
        headers.add("Origin", "https://buff.163.com");
        ManualPlusRoot manualPlusRoot = new ManualPlusRoot();
        manualPlusRoot.setAssets(assets);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(manualPlusRoot, headers);
        String url = "https://buff.163.com/api/market/sell_order/create/manual_plus";
        ResponseEntity<String> responseEntity1 = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        log.info("1111");
    }
}
