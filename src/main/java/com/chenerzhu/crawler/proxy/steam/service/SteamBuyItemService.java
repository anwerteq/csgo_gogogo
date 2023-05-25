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

    public void createbuyorder(String price_total, String market_hash_name) {
        CreatebuyorderEntity createbuyorderEntity = new CreatebuyorderEntity();
        createbuyorderEntity.setMarket_hash_name(market_hash_name);
        createbuyorderEntity.setPrice_total(price_total);
        Map<String, String> saleHeader = SteamConfig.getBuyHeader();
        saleHeader.put("Referer", "https://steamcommunity.com/market/listings/730/" + URLEncoder.encode(market_hash_name));
        for (String cookie : saleHeader.get("Cookie").split(";")) {
            if ("sessionid".equals(cookie.split("=")[0].trim())) {
                createbuyorderEntity.setSessionid(cookie.split("=")[1].trim());
                break;
            }
        }
        String url = "https://steamcommunity.com/market/createbuyorder/";  // post ,x-www
        String responseStr = HttpClientUtils.sendPostForm(url, JSONObject.toJSONString(createbuyorderEntity), saleHeader,new HashMap<>());
        log.info("steam下求购订单返回的数据为："+responseStr);
    }
}
