package com.chenerzhu.crawler.proxy.steam.service;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.steam.CreatebuyorderEntity;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * steam购买商品逻辑
 */
@Service
@Slf4j
public class SteamBuyItemService {


    /**
     * 提交订单
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
        log.info("steam下求购订单返回的数据为："+responseStr);
    }
}
