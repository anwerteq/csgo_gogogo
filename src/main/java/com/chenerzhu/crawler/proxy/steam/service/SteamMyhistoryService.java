package com.chenerzhu.crawler.proxy.steam.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.Assets;
import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.entity.SteamMyhistoryRoot;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * steam历史记录
 */
@Service
@Slf4j
public class SteamMyhistoryService {


    public void marketMyhistory(int start){
        String url = "https://steamcommunity.com/market/myhistory/render/?query=&count=10&start=" + 0;
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        SteamMyhistoryRoot steamMyhistoryRoot = JSONObject.parseObject(resStr, SteamMyhistoryRoot.class);

        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(steamMyhistoryRoot.getAssets()));
        JSONObject jsonObject1 = jsonObject.getJSONObject("730").getJSONObject("2");
        Map<String, Assets> keyAndAssets = new HashMap<>();
        for (Map.Entry<String, Object> entry : jsonObject1.entrySet()) {
            JSONObject values =   (JSONObject)entry.getValue();
            addKeyAndAssets(values,keyAndAssets);
        }

        String results_html = steamMyhistoryRoot.getResults_html();
        Document parse = Jsoup.parse(results_html);
        log.info("123123");
    }

    public void addKeyAndAssets(JSONObject values,Map<String, Assets> keyAndAssets){
        JSONArray actions = values.getJSONArray("actions");
        String key =  actions.getString(0).split("preview%20M")[1].split("A%assetid%")[0];
        String elementId = "history_row_4"+key+"_event_1";
        Assets assets = new Assets();
        assets.setAssetid(values.getString("id"));
        assets.setClassid(values.getString("classid"));

        keyAndAssets.put(elementId,assets);
        log.info("123123");

    }
}
