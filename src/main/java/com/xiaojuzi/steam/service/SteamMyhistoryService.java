package com.xiaojuzi.steam.service;


import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xiaojuzi.steam.SteamConfig;
import com.xiaojuzi.steam.dto.MarketListingResponse;
import com.xiaojuzi.steam.entity.CZ75Item;
import com.xiaojuzi.steam.entity.SteamMyhistoryRoot;
import com.xiaojuzi.steam.repository.CZ75ItemRepository;
import com.xiaojuzi.steam.service.steamrenderhistory.SteamAsset;
import com.xiaojuzi.steam.util.SleepUtil;
import com.xiaojuzi.util.HttpClientUtils;
import com.xiaojuzi.util.SteamTheadeUtil;
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
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * steam历史记录
 */
@Service
@Slf4j
public class SteamMyhistoryService {

    @Autowired
    SteamBuyItemService steamBuyItemService;

    @Autowired
    CZ75ItemRepository cz75ItemRepository;


    public void marketMyhistorys() {
        int start = 1;
        while (true) {
            log.info("start的值为：{}", start);
            Boolean marketMyhistory = getMarketMyhistory(start,500);
            if (marketMyhistory == null) {

            }else if (marketMyhistory) {
                start ++;
            }else {
                return;
            }

        }
    }

    /**
     * 拉取一页数据
     * @param page_no
     * @param page_size
     * @return
     */
    public Boolean getMarketMyhistory(int page_no,int page_size) {
        String url = "https://steamcommunity.com/market/myhistory/render/?query=&count="+page_size +"&l=schinese&start=" + (page_no-1) *page_size;
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        MarketListingResponse steamMyhistoryRoot = null;
        try {
            steamMyhistoryRoot = JSON.parseObject(resStr, MarketListingResponse.class);
        }catch (JSONException e){
            log.error("getMarketMyhistory 序列化失败，进行序列化信息为："+resStr);
            ThreadUtil.sleep(30000);
            return false;
        }
        if (steamMyhistoryRoot.getTotal_count() == 0){
            return null;
        }
        Map<String, JSONObject> hisotryPrice = getHisotryPrice(steamMyhistoryRoot.getResults_html(),steamMyhistoryRoot.getHovers());
        List<CZ75Item> cz75ItemList = change2CZ75ItemList(steamMyhistoryRoot.getAssets());
        Map<Long, CZ75Item> idAndCZ75Item = cz75ItemList.stream().collect(Collectors.toMap(CZ75Item::getId, o2 -> o2));
        String steamID = SteamTheadeUtil.getThreadSteamUserDate().getSession().getSteamID();
        //只遍历 购买和售出的
        List<CZ75Item> collect = hisotryPrice.entrySet().stream().map(entry -> {
            CZ75Item cz75Item = idAndCZ75Item.get(Long.parseLong(entry.getKey()));
            JSONObject jsonObject = entry.getValue();
            cz75Item.setUsd(jsonObject.getDouble("price"));
            cz75Item.setListingDate(jsonObject.getString("listingDate"));
            cz75Item.setTradingDate(jsonObject.getString("tradingDate"));
            cz75Item.setMemo(jsonObject.getString("memoDate"));
            cz75Item.setTheTypeOfTransaction(jsonObject.getString("theTypeOfTransaction"));
            cz75Item.setSteamId(steamID);
            cz75Item.refreshSteamInventoryMarkId();
            return cz75Item;
        }).collect(Collectors.toList());
        log.info("保存的steam市场信息，共："+collect.size()+"条");
        CompletableFuture.supplyAsync(()->cz75ItemRepository.saveAll(collect));
        SleepUtil.sleep(3000);
        return true;
    }

    /**
     * 建立饰品价格的关系
     *
     * @param hovers
     * @return
     */
    private Map<String, String> paraseHovers(String hovers) {
        Map<String, String> htmlRowIdAndhId = new HashMap<>();
        String[] split = hovers.split(";");
        for (String row : split) {
            row = row.trim();
            if (StrUtil.isBlank(row)) {
                continue;
            }
            String[] split1 = row.split(",");
            String htmlRowId = split1[1].replaceAll("'","");;
            String id = split1[4].replaceAll("'","");
            htmlRowIdAndhId.put(htmlRowId.replaceAll("_name","").trim(), id.trim());
        }
        return htmlRowIdAndhId;
    }


    /**
     * 解析购买成本数据
     *
     * @param page_index
     */
    public List<SteamAsset> marketMyhistorys(int page_index) {
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
            return new ArrayList<>();
        }
        //div id 和 价格的映射
        Map<String, Double> historyRowAndPrice = getHisotryBuyPrice(steamMyhistoryRoot.getResults_html());
        //没有购买的数据
        if (historyRowAndPrice.isEmpty()) {
            return new ArrayList<>();
        }
        //div id和 assetId的映射
        Map<String, String> historyRowAndAssestIdMap = parseHovers(steamMyhistoryRoot.getHovers());

        //构建assetid和price的映射
        Map<String, String> assetIdAndPriceMap = buildAssetIdAndPriceMap(historyRowAndAssestIdMap, historyRowAndPrice);
        //饰品的数据
        JSONObject asset2JSONObject = parseAsset2JSONObject(steamMyhistoryRoot.getAssets());
        List<SteamAsset> steamAssets = new ArrayList();
        for (Map.Entry<String, String> entry : assetIdAndPriceMap.entrySet()) {
            String assetId = entry.getKey();
            JSONObject assetJSONObject = asset2JSONObject.getJSONObject(assetId);
            if (ObjectUtil.isNull(assetJSONObject)) {
                continue;
            }
            String string = JSONObject.toJSONString(assetJSONObject);
            SteamAsset steamAsset = JSONObject.parseObject(string, SteamAsset.class);
            Object actions = assetJSONObject.getJSONArray("actions").get(0);
            String link = ((JSONObject) actions).getString("link");
            link = link.replace("%assetid%", assetId);
            steamAsset.setLink(link);
            steamAsset.setPrice(entry.getValue());
            steamAssets.add(steamAsset);
        }
        return steamAssets;
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
     * 转化成 集合
     *
     * @param assets
     */
    public List<CZ75Item> change2CZ75ItemList(Map<String, Map<String, Map<String, CZ75Item>>> assets) {
        List<CZ75Item> cz75ItemList = new ArrayList<>();
        for (Map.Entry<String, Map<String, Map<String, CZ75Item>>> gameEntry : assets.entrySet()) {
            // 游戏id
            String gamekey = gameEntry.getKey();
            for (Map.Entry<String, Map<String, CZ75Item>> equipmentEntry : gameEntry.getValue().entrySet()) {
                // 区分是装备商品还是什么
                String equipmentkey = equipmentEntry.getKey();
                Map<String, CZ75Item> value = equipmentEntry.getValue();
                for (Map.Entry<String, CZ75Item> entry : value.entrySet()) {
                    cz75ItemList.add(entry.getValue());
                }
            }
        }
        return  cz75ItemList;
    }

    /**
     * 获取历史购买和销售价格： 负数：购买价格，整数： 销售价格
     *
     * @param body
     * @return
     */
    public Map<String, JSONObject> getHisotryPrice(String body,String hovers) {
        // 建立饰品价格的关系
        Map<String, String> htmlRowIdAndhId = paraseHovers(hovers);
        Map<String, JSONObject> keyAndPrice = new HashMap<>();
        Document parse = Jsoup.parse(body);
        Element child = parse.child(0).child(1);
        Elements historyPriceRows = child.getElementsByClass("market_listing_row market_recent_listing_row");
        JSONObject jsonObject = new JSONObject();
        //历史价格行数
        for (Element historyPriceRow : historyPriceRows) {
            String name_block = historyPriceRow.getElementsByClass("market_listing_whoactedwith").text();
            if ("物品上架".equals(name_block) || "物品下架".equals(name_block) || "上架过期".equals(name_block)) {
                continue;
            }
            //含有交易时间的 分为两种： 1：上市交易的，2：购买的（仓库中的存在这）
            String price = historyPriceRow.getElementsByClass("market_listing_right_cell market_listing_their_price").text();
            price = price.replace("$", "");
            jsonObject.put("price", price);
            // 交易日期
            String tradingDate = historyPriceRow.getElementsByClass("market_listing_right_cell market_listing_listed_date can_combine").get(0).text();
            jsonObject.put("tradingDate", tradingDate);

            // 上架日期
            String listingDate = historyPriceRow.getElementsByClass("market_listing_right_cell market_listing_listed_date can_combine").get(1).text();
            jsonObject.put("listingDate", listingDate);

            // 交易备注
            String memoDate = historyPriceRow.getElementsByClass("market_listing_listed_date_combined").get(0).text();
            jsonObject.put("memoDate", memoDate);
            if (memoDate.contains("购买")){
                jsonObject.put("theTypeOfTransaction","买");
            }else if (memoDate.contains("售出")){
                jsonObject.put("theTypeOfTransaction","卖");
            }else {
                System.out.println("");
            }
            String htmlId = historyPriceRow.id();
            String id = htmlRowIdAndhId.get(htmlId);
            // 存在 history_row_4390503095263598883_event_2，饰品过期，自动下架。
            keyAndPrice.put(id, jsonObject);
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
            if ("物品上架".equals(name_block) || "物品下架".equals(name_block)) {
                continue;
            }
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
