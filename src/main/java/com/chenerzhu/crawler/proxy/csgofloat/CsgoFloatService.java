package com.chenerzhu.crawler.proxy.csgofloat;


import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.Items;
import com.chenerzhu.crawler.proxy.steam.service.csgoFloat.FloatBulk;
import com.chenerzhu.crawler.proxy.steam.service.marketlist.SteamLossItemDetail;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CsgoFloatService {


    /**
     * 获取磨损数据数据
     *
     * @param details
     * @return
     */
    public List<SteamLossItemDetail> postBulk(List<SteamLossItemDetail> details) {
        List<Map<String, String>> links = new ArrayList<>();
        for (SteamLossItemDetail detail : details) {
            Map<String, String> hashMap = new HashMap();
            hashMap.put("link", detail.getUrl());
            links.add(hashMap);
        }
        Map<String, Double> linkAndFloatValueMap = getListIdAndWearMap(links);
        for (SteamLossItemDetail detail : details) {
            String linkKey = detail.getLinkKey();
            detail.setPainwear(String.valueOf(linkAndFloatValueMap.get(linkKey)));
        }
        return details;
    }


    /**
     * 获取磨损数据数据
     *
     * @param items
     * @return
     */
    public List<Items> postBuffBulks(List<Items> items) {
        List<Items> list = new ArrayList<>();
        for (Items item : items) {
            list.add(item);
            if (list.size() > 40) {
                postBuffBulk(list);
                list.clear();
            }
        }
        postBuffBulk(list);
        return items;
    }

    /**
     * 获取磨损数据数据
     *
     * @param items
     * @return
     */
    public List<Items> postBuffBulk(List<Items> items) {
        List<Map<String, String>> links = new ArrayList<>();
        for (Items detail : items) {
            Map<String, String> hashMap = new HashMap();
            hashMap.put("link", detail.getAction_link());
            links.add(hashMap);
        }
        Map<String, Double> linkAndFloatValueMap = getListIdAndWearMap(links);
        for (Items detail : items) {
            String url = detail.getAction_link();
            for (Map.Entry<String, Double> entry : linkAndFloatValueMap.entrySet()) {
                String key = "A" + entry.getKey() + "D";
                if (url.contains(key)) {
                    detail.setPainwear(String.valueOf(entry.getValue()));
                }
            }
        }
        return items;
    }

    /**
     * 获取steam磨损数据数据
     *
     * @param urlAndPriceMap
     * @return
     */
    public Map<String, String> postBulks(Map<String, String> urlAndPriceMap) {
        //磨损度和价格的映射
        Map<String, String> wearAndPriceMap = new HashMap<>();
        Map<String, String> tempUrlAndPriceMap = new HashMap<>();
        for (Map.Entry<String, String> entry : urlAndPriceMap.entrySet()) {
            tempUrlAndPriceMap.put(entry.getKey(), entry.getValue());
            if (tempUrlAndPriceMap.size() > 40) {
                Map<String, String> map = postBulk(tempUrlAndPriceMap);
                wearAndPriceMap.putAll(map);
                tempUrlAndPriceMap.clear();
            }
        }
        Map<String, String> map = postBulk(tempUrlAndPriceMap);
        wearAndPriceMap.putAll(map);
        return wearAndPriceMap;
    }

    /**
     * 获取磨损数据数据
     *
     * @param urlAndPriceMap
     * @return
     */
    public Map<String, String> postBulk(Map<String, String> urlAndPriceMap) {
        List<Map<String, String>> links = new ArrayList<>();
        for (Map.Entry<String, String> entry : urlAndPriceMap.entrySet()) {
            Map<String, String> hashMap = new HashMap();
            hashMap.put("link", entry.getKey());
            links.add(hashMap);
        }
        // a 和磨损度
        Map<String, Double> listIdAndFloatValueMap = getListIdAndWearMap(links);
        //磨损度和价格的映射
        Map<String, String> wearAndPriceMap = new HashMap<>();
        for (Map.Entry<String, Double> entry : listIdAndFloatValueMap.entrySet()) {
            String linkKey = entry.getKey();
            String key = "A" + linkKey + "D";
            for (Map.Entry<String, String> urlMapEntry : urlAndPriceMap.entrySet()) {
                if (urlMapEntry.getKey().contains(key)) {
                    wearAndPriceMap.put(String.valueOf(entry.getValue()), urlMapEntry.getValue());
                }
            }
        }
        return wearAndPriceMap;
    }


    /**
     * 根据url获取磨损度
     *
     * @param links
     * @return
     */
    public Map<String, Double> getListIdAndWearMap(List<Map<String, String>> links) {
        String url = "http://localhost:8086/bulk";
        HashMap hashMap = new HashMap();
        hashMap.put("links", links);
        String reponse = HttpClientUtils.sendPost(url, JSONObject.toJSONString(hashMap), new HashMap<>());
        JSONObject jsonObject = JSONObject.parseObject(reponse);
        //磨损信息
        List<FloatBulk> floatBulks = jsonObject.entrySet().stream().map(entrySet -> {
            FloatBulk floatBulk = JSONObject.parseObject(JSONObject.toJSONString(entrySet.getValue()), FloatBulk.class);
            floatBulk.setLinkKey(entrySet.getKey());
            return floatBulk;
        }).collect(Collectors.toList());
        Map<String, Double> linkAndFloatValueMap = floatBulks.stream().collect(Collectors.toMap(FloatBulk::getLinkKey, FloatBulk::getFloatvalue));
        return linkAndFloatValueMap;
    }
}
