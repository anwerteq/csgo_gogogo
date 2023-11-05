package com.chenerzhu.crawler.proxy.steam.service;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.entity.SteamCostEntity;
import com.chenerzhu.crawler.proxy.steam.entity.SteamMyhistoryRoot;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * steam历史记录
 */
@Service
@Slf4j
public class SteamMyhistoryService {

    @Autowired
    SteamBuyItemService steamBuyItemService;


    public void marketMyhistorys() {
//        int start = 7030;
//        int start = 5730;
//        int start = 4230;
        int start = 950;
        while (start > 0) {
            log.info("start的值为：{}", start);
            marketMyhistory(start);
            start = start - 10;
        }
    }

    /**
     * 拉取一页数据
     *
     * @param start
     */
    public void marketMyhistory(int start) {
        String url = "https://steamcommunity.com/market/myhistory/render/?query=&count=10&start=" + start;
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        SteamMyhistoryRoot steamMyhistoryRoot = JSONObject.parseObject(resStr, SteamMyhistoryRoot.class);

        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(steamMyhistoryRoot.getAssets()));
        JSONObject jsonObject1 = jsonObject.getJSONObject("730").getJSONObject("2");
        Map<String, SteamCostEntity> mapSteamCostEntity = getMapSteamCostEntity(jsonObject1);
        Map<String, Double> hisotryPrice = getHisotryPrice(steamMyhistoryRoot.getResults_html());
        //销售记录
        List<SteamCostEntity> sellCost = new ArrayList<>();
        //购买集合
        List<SteamCostEntity> buyCost = new ArrayList<>();
        for (Map.Entry<String, Double> entry : hisotryPrice.entrySet()) {
            //美元变美分
            Double value = entry.getValue() * 100;
            if (value == 0) {
                continue;
            }
            SteamCostEntity steamCostEntity = mapSteamCostEntity.get(entry.getKey());
            if (entry.getValue() > 0) {
                //销售金额
                steamCostEntity.setReturned_money(value.intValue());
                sellCost.add(steamCostEntity);
            } else {
                //购买金额
                steamCostEntity.setSteam_cost(-value.intValue());
                buyCost.add(steamCostEntity);
            }
        }
        SleepUtil.sleep(3000);
        sellCost.forEach(steamBuyItemService::saveForsellPrice);
        buyCost.forEach(steamBuyItemService::saveForCostPrice);
    }


    /**
     * 解析购买成本数据
     *
     * @param page_index
     */
    public Map<String, String> marketMyhistorys(int page_index) {
        log.info("开始拉取steam交易记录的第:{}页", page_index);
        int count = 500;
        int start = (page_index - 1) * count;
        String url = "https://steamcommunity.com/market/myhistory/render/?query=&count="
                + count + "&start=" + start;
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        SteamMyhistoryRoot steamMyhistoryRoot = JSONObject.parseObject(resStr, SteamMyhistoryRoot.class);
        if (steamMyhistoryRoot.getTotal_count() <= start) {
            return null;
        }
        if (StrUtil.isEmpty(steamMyhistoryRoot.getHovers())) {
            return new HashMap<>();
        }
        //div id 和 价格的映射
        Map<String, Double> historyRowAndPrice = getHisotryBuyPrice(steamMyhistoryRoot.getResults_html());
        //没有购买的数据
        if (historyRowAndPrice.isEmpty()) {
            return new HashMap<>();
        }
        //div id和 assetId的映射
        Map<String, String> historyRowAndAssestIdMap = parseHovers(steamMyhistoryRoot.getHovers());

        //构建assetid和price的映射
        Map<String, String> assetIdAndPriceMap = buildAssetIdAndPriceMap(historyRowAndAssestIdMap, historyRowAndPrice);
        //饰品的数据
        JSONObject asset2JSONObject = parseAsset2JSONObject(steamMyhistoryRoot.getAssets());
        Map<String, String> itemOnlyKeyAndPriceMap = new HashMap<>();
        for (Map.Entry<String, String> entry : assetIdAndPriceMap.entrySet()) {
            String assetId = entry.getKey();
            JSONObject assetJSONObject = asset2JSONObject.getJSONObject(assetId);
            if (ObjectUtil.isNull(assetJSONObject)) {
                continue;
            }
            Object actions = assetJSONObject.getJSONArray("actions").get(0);
            String link = ((JSONObject) actions).getString("link");
            link = link.replace("%assetid%", assetId);
            itemOnlyKeyAndPriceMap.put(link, entry.getValue());
        }
        return itemOnlyKeyAndPriceMap;
    }

    /**
     * 构建assetid和price的映射
     *
     * @param historyRowAndAssestIdMap
     * @param historyRowAndPrice
     * @return
     */
    public Map<String, String> buildAssetIdAndPriceMap(Map<String, String> historyRowAndAssestIdMap, Map<String, Double> historyRowAndPrice) {
        Map<String, String> hashMap = new HashMap();
        for (Map.Entry<String, Double> entry : historyRowAndPrice.entrySet()) {
            Double price = entry.getValue();
            String assetId = historyRowAndAssestIdMap.getOrDefault(entry.getKey() + "_image", "");
            hashMap.put(assetId, String.valueOf(price));
        }
        return hashMap;
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
            String elementId = "history_row_" + key + "_" + (Long.valueOf(key) + 1);
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

    /**
     * 将assets数据转化成json数据
     *
     * @return
     */
    public JSONObject parseAsset2JSONObject(Object obj) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(obj));
        JSONObject jsonObject1 = jsonObject.getJSONObject("730").getJSONObject("2");
        return jsonObject1;
    }


    /**
     * 获取历史购买和销售价格： 负数：购买价格，整数： 销售价格
     *
     * @param body
     * @return
     */
    public Map<String, Double> getHisotryBuyPrice(String body) {
        Map<String, Double> historyRowAndPrice = new HashMap<>();
        Document parse = Jsoup.parse(body);
        Element child = parse.child(0).child(1);
        Elements historyPriceRows = child.getElementsByClass("market_listing_row market_recent_listing_row");
        //历史价格行数
        for (Element historyPriceRow : historyPriceRows) {
            String name_block = historyPriceRow.getElementsByClass("market_listing_whoactedwith_name_block").text();
            //购买的商品
            if (!name_block.contains("卖家") && !name_block.contains("Seller")) {
                continue;
            }
            String price = historyPriceRow.getElementsByClass("market_listing_their_price").text();
            price = price.replace("$", "");
            String id = historyPriceRow.id();
            historyRowAndPrice.put(id, Double.valueOf(price));
        }
        return historyRowAndPrice;
    }


    /**
     * 解析div和数据的映射数据
     *
     * @param hovers
     * @return
     */
    public Map<String, String> parseHovers(String hovers) {
        // CreateItemHoverFromContainer( g_rgAssets, 'history_row_5831654360491315109_5831654360491315110_name', 730, '2', '33254416435', 0 )
        // 使用正则表达式提取参数
        String pattern = "CreateItemHoverFromContainer\\(\\s*g_rgAssets, '([^']*)', (\\d+), '([^']*)', '([^']*)', (\\d+) \\);";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(hovers);
        Map<String, String> historyRowAndAssestIdMap = new HashMap();
        // 提取的参数存储在列表中
        while (matcher.find()) {
            String history_row_id = matcher.group(1);
            String number1 = matcher.group(2);
            String number2 = matcher.group(3);
            String assertId = matcher.group(4);
            String number4 = matcher.group(5);
            historyRowAndAssestIdMap.put(history_row_id, assertId);
        }
        return historyRowAndAssestIdMap;
    }

}
