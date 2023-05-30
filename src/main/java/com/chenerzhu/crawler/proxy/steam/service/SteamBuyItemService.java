package com.chenerzhu.crawler.proxy.steam.service;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.steam.CreatebuyorderEntity;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.entity.SteamCostEntity;
import com.chenerzhu.crawler.proxy.steam.repository.SteamCostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * steam购买商品逻辑
 */
@Service
@Slf4j
public class SteamBuyItemService {


    @Autowired
    SteamCostRepository steamCostRepository;

    /**
     * 提交steam订单
     * @param price_total
     * @param market_hash_name
     */
    public void createbuyorder(String price_total, String market_hash_name) {
        CreatebuyorderEntity createbuyorderEntity = new CreatebuyorderEntity();
        createbuyorderEntity.setMarket_hash_name(market_hash_name);
        createbuyorderEntity.setPrice_total(price_total);
        createbuyorderEntity.setSessionid(SteamConfig.getCookieOnlyKey("sessionid"));
        Map<String, String> saleHeader = SteamConfig.getBuyHeader();
        String url = "https://steamcommunity.com/market/createbuyorder/";  // post ,x-www
        String responseStr = HttpClientUtils.sendPostForm(url, JSONObject.toJSONString(createbuyorderEntity), saleHeader,new HashMap<>());
        saveSteamCostEntity(createbuyorderEntity);
        log.info("steam下求购订单返回的数据为："+responseStr);
    }


    /**
     * 保存steam商品购买信息
     * @param buyOrderEntity
     */
    public void saveSteamCostEntity( CreatebuyorderEntity buyOrderEntity){
        SteamCostEntity steamCostEntity = new SteamCostEntity();
        steamCostEntity.setCostId(UUID.randomUUID().toString());
        steamCostEntity.setSteam_cost(Double.parseDouble(buyOrderEntity.getPrice_total()));
        steamCostEntity.setHash_name(buyOrderEntity.getMarket_hash_name());
        steamCostEntity.setCreate_time(new Date());
        steamCostEntity.setBuy_status(0);
        steamCostRepository.save(steamCostEntity);
    }
}
