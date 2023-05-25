package com.chenerzhu.crawler.proxy.steam.service;

import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * steam确认收货
 */
@Service
public class TradeofferService {

    /**
     * 获取steam交易用户的id
     * * @param tradeIds
     */
    public void tradeoffers(Set<String> tradeIds){
        Object[] objects = tradeIds.toArray();
        String url = "https://steamcommunity.com/tradeoffer/"+objects[0];
        Map<String, String> saleHeader = SteamConfig.getSteamHeader();
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        Document parse = Jsoup.parse(resStr);
        System.out.println("123123");
    }


}
