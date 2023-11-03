package com.chenerzhu.crawler.proxy.steam.service;

import cn.hutool.core.util.URLUtil;
import com.chenerzhu.crawler.proxy.common.GameCommet;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * steam扫低磨损代码
 */
@Service
@Slf4j
public class SteamLossPaintwearService {


    /**
     * 获取steam市场数据
     */
    public void getMarketLists(String hashName){

        String hashNameUrl = URLUtil.encode(hashName, "UTF-8").replace("+", "%20");
        String url = "https://steamcommunity.com/market/listings/" + GameCommet.getGameId() + "/" + hashNameUrl;
        Map<String, String> saleHeader = SteamConfig.getSteamHeader();
        String responseStr = HttpClientUtils.sendGet(url, saleHeader);

    }
}
