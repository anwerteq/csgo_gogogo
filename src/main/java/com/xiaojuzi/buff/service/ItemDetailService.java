package com.xiaojuzi.buff.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaojuzi.buff.BuffConfig;
import com.xiaojuzi.csgo.entity.ItemGoods;
import com.xiaojuzi.steam.service.SteamLossPaintwearService;
import com.xiaojuzi.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * buff商品明细服务
 */
@Service
@Slf4j
public class ItemDetailService {


    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SteamInventorySerivce steamInventorySerivce;

    @Autowired
    SteamLossPaintwearService steamLossPaintwearService;

    /**
     * 获取这个商品的磨损区间
     *
     * @param goodId
     * @return
     */
    public List<String> getWearInterval(String goodId) {
        List<String> wearIntervals = new ArrayList<>();
        String url = "https://buff.163.com/goods/" + goodId + "?from=market";
        SleepUtil.sleep(5000);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        if (responseEntity.getStatusCode().value() == 302) {
            return null;
        }
        String body = responseEntity.getBody();
        try {
            String wearStr = body.split(" paintwear_choices: ")[1].split(",\n" +
                    "             ")[0];
            JSONArray objects = JSONObject.parseArray(wearStr);
            for (Object object : objects) {
                JSONArray JSONArray1 = (JSONArray) object;
                StringJoiner stringJoiner = new StringJoiner("-");
                for (Object o : JSONArray1) {
                    stringJoiner.add(String.valueOf(o.toString()));
                }
                wearIntervals.add(stringJoiner.toString());
            }
        } catch (Exception e) {
            log.error("获取饰品的磨损区间失败", e);
            return new ArrayList<>();
        }
        return wearIntervals;
    }

    /**
     * 获取区间内，最低的价格
     *
     * @param goodId
     * @param paintwearList
     * @return
     */
    public Map<String, String> getSellPrices(String goodId, List<String> paintwearList) {
        Map<String, String> painwearMap = new HashMap();
        for (String paintwear : paintwearList) {
            String sellPrice = steamInventorySerivce.getSellPrices(goodId, paintwear);
            painwearMap.put(paintwear, sellPrice);
        }
        return painwearMap;
    }

    /**
     * 构建 扫steam低磨损的数据
     */
    public void autoButSteam(ItemGoods itemGoods) {
        List<String> wearIntervalList = getWearInterval(itemGoods.getMarketHashName());
        Map<String, String> sellPrices = getSellPrices(itemGoods.getMarketHashName(), wearIntervalList);
        steamLossPaintwearService.getMarketLists(itemGoods, sellPrices);
    }




}
