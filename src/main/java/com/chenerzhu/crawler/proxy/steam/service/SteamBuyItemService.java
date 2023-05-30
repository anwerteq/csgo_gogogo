package com.chenerzhu.crawler.proxy.steam.service;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.ExecutorUtil;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.Assets;
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
     * @param price_total：美分
     * @param market_hash_name
     */
    public void createbuyorder(Double price_total, String market_hash_name) {
        CreatebuyorderEntity createbuyorderEntity = new CreatebuyorderEntity();
        createbuyorderEntity.setMarket_hash_name(market_hash_name);
        createbuyorderEntity.setPrice_total(String.valueOf(price_total.intValue()));
        createbuyorderEntity.setSessionid(SteamConfig.getCookieOnlyKey("sessionid"));
        Map<String, String> saleHeader = SteamConfig.getBuyHeader();
        saleHeader.put("Referer","https://steamcommunity.com/market/listings/730/" + URLEncoder.encode(market_hash_name));
        HashMap hashMap = JSONObject.parseObject(JSONObject.toJSONString(createbuyorderEntity), HashMap.class);
        String url = "https://steamcommunity.com/market/createbuyorder";
        // post ,x-www
        String responseStr = HttpClientUtils.sendPostForm(url, "", saleHeader,hashMap);
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        Object success = jsonObject.get("success");
        if (success.toString().compareTo("1") >= 1){
            log.info("steam下求购订单success返回的数据为："+responseStr);
            return;
        }
        ExecutorUtil.pool.execute(()->{
            saveSteamCostEntity(createbuyorderEntity);
        });
        log.info("steam下求购订单返回的数据为："+responseStr);
    }


    /**
     * 保存steam商品购买信息
     * @param buyOrderEntity
     */
    public void saveSteamCostEntity( CreatebuyorderEntity buyOrderEntity){
        SteamCostEntity steamCostEntity = new SteamCostEntity();
        steamCostEntity.setCostId(UUID.randomUUID().toString());
        steamCostEntity.setSteam_cost(Double.valueOf(buyOrderEntity.getPrice_total()));
        steamCostEntity.setHash_name(buyOrderEntity.getMarket_hash_name());
        steamCostEntity.setCreate_time(new Date());
        steamCostEntity.setBuy_status(0);
        steamCostRepository.save(steamCostEntity);
    }


    public void updateSteamCostEntity(Assets assets,SteamCostEntity steamCostEntity,String name){
        steamCostEntity.setUpdate_time(new Date());
        steamCostEntity.setBuy_status(1);
        steamCostEntity.setClassid(assets.getClassid());
        steamCostEntity.setAssetid(assets.getAssetid());
        steamCostEntity.setName(name);
        steamCostRepository.save(steamCostEntity);
    }
}
