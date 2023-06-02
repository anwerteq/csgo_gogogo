package com.chenerzhu.crawler.proxy.steam.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.entity.SteamCostEntity;
import com.chenerzhu.crawler.proxy.steam.entity.SteamMyhistoryRoot;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * steam历史记录
 */
@Service
@Slf4j
public class SteamMyhistoryService {


    public void marketMyhistory(int start) {
        String url = "https://steamcommunity.com/market/myhistory/render/?query=&count=10&start=" + 20;
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        SteamMyhistoryRoot steamMyhistoryRoot = JSONObject.parseObject(resStr, SteamMyhistoryRoot.class);

        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(steamMyhistoryRoot.getAssets()));
        JSONObject jsonObject1 = jsonObject.getJSONObject("730").getJSONObject("2");
        Map<String, SteamCostEntity> mapSteamCostEntity = getMapSteamCostEntity(jsonObject1);
        Map<String, Double> hisotryPrice = getHisotryPrice(steamMyhistoryRoot.getResults_html());

        log.info("123123");
    }

    /**
     * 拼接数据和售卖价格的映射关系
     *
     * @param jsonObject1
     */
    public Map<String, SteamCostEntity> getMapSteamCostEntity(JSONObject jsonObject1) {
        Map<String, SteamCostEntity> keyAndAssets = new HashMap<>();
        for (Map.Entry<String, Object> entry : jsonObject1.entrySet()) {
            JSONObject values = (JSONObject) entry.getValue();
            JSONArray actions = values.getJSONArray("actions");
            if (actions == null) {
                continue;
            }

            String key = actions.getString(0).split("preview%20M")[1].split("A%assetid%")[0];
            String elementId = "history_row_4" + key + "_" + "event_1";
            SteamCostEntity steamCostEntity = new SteamCostEntity();
            steamCostEntity.setClassid(values.getString("classid"));
            steamCostEntity.setAssetid(values.getString("id"));
            steamCostEntity.setName(values.getString("name"));
            steamCostEntity.setHash_name(values.getString("market_hash_name"));
            keyAndAssets.put(elementId, steamCostEntity);
        }
        return keyAndAssets;
    }


    /**
     * 获取历史购买和销售价格： 负数：购买价格，整数： 销售价格
     *
     * @param body
     * @return
     */
    public Map<String, Double> getHisotryPrice(String body) {
        Map<String, Double> keyAndPrice = new HashMap<>();
        Document parse = Jsoup.parse(body);
        Element child = parse.child(0).child(1);
        Elements historyPriceRows = child.getElementsByClass("market_listing_row market_recent_listing_row");
        //历史价格行数
        for (Element historyPriceRow : historyPriceRows) {
            String price = historyPriceRow.getElementsByClass("market_listing_right_cell market_listing_their_price").text();
            price = price.replace("$", "");
            String name_block = historyPriceRow.getElementsByClass("market_listing_whoactedwith_name_block").text();
            String id = historyPriceRow.id();
            //购买的商品
            if (name_block.contains("卖家")) {
                price = "-" + price;
            } else if (name_block.contains("买家")) {
                //销售的价格
            } else {
                price = "0";
            }
            keyAndPrice.put(id, Double.valueOf(price));
        }
        return keyAndPrice;
    }
}
