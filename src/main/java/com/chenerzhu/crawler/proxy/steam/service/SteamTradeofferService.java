package com.chenerzhu.crawler.proxy.steam.service;

import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
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
            SleepUtil.sleep(2000);
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
        paramerMap.put("sessionid", SteamConfig.getCookieOnlyKey("sessionid"));
        paramerMap.put("serverid","1");
        paramerMap.put("captcha","");
        paramerMap.put("tradeofferid",tradeofferid);
        paramerMap.put("partner",partner);
        String responseStr = HttpClientUtils.sendPostForm(url, "", saleHeader, paramerMap);
        log.info("确认收货{}：,接口返回的参数{}",partner,responseStr);
    }

}
