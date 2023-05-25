package com.chenerzhu.crawler.proxy.steam.service;

import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * steam确认收货
 */
@Service
@Slf4j
public class SteamTradeofferService {

    /**
     * 获取steam交易用户的id
     * * @param tradeIds
     */
    public String getPartner(String tradeId) {
        String url = "https://steamcommunity.com/tradeoffer/" + tradeId;
        Map<String, String> saleHeader = SteamConfig.getSteamHeader();
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        String[] split = resStr.split("var g_ulTradePartnerSteamID = '");
        String[] split1 = split[1].split("';");
        return split1[0];
    }

    /**
     * steam接收订单操作
     */
    public void steamaccept(Set<String> tradeIds) {

        tradeIds.stream().forEach(tradeId -> {
            String partner = getPartner(tradeId);
            doSteamaccept(partner,tradeId);
        });
    }

    /**
     * 开始发送steam确认请求
     * @param tradeofferid
     * @param partner
     */
    public void doSteamaccept(String partner , String tradeofferid) {
        String url = "https://steamcommunity.com/tradeoffer/"+tradeofferid+"/accept";
        Map<String, String> paramerMap = new HashMap<>();
        Map<String, String> saleHeader = SteamConfig.getSaleHeader();
        saleHeader.put("Referer","https://steamcommunity.com/tradeoffer/"+tradeofferid);
        for (String cookie : saleHeader.get("Cookie").split(";")) {
            if ("sessionid".equals(cookie.split("=")[0].trim())) {
                paramerMap.put("sessionid", cookie.split("=")[1].trim());
                break;
            }
        }
        paramerMap.put("serverid","1");
        paramerMap.put("captcha","");
        paramerMap.put("tradeofferid",tradeofferid);
        paramerMap.put("partner",partner);
        String responseStr = HttpClientUtils.sendPostForm(url, "", saleHeader, paramerMap);
        log.info("确认收货："+partner);
    }

}
