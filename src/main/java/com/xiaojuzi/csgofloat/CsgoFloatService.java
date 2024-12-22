package com.xiaojuzi.csgofloat;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.xiaojuzi.csgo.BuffBuyItemEntity.Items;
import com.xiaojuzi.steam.service.csgoFloat.FloatBulk;
import com.xiaojuzi.steam.service.marketlist.SteamLossItemDetail;
import com.xiaojuzi.steam.service.steamrenderhistory.SteamAsset;
import com.xiaojuzi.steam.util.SleepUtil;
import com.xiaojuzi.util.HttpClientUtils;
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
    public List<SteamLossItemDetail> postLossBulk(List<SteamLossItemDetail> details) {
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
        int count = 0;
        for (Items item : items) {
            list.add(item);
            if (list.size() > 40) {
                log.info("第[{}]页,buff数据和steam数据进行关联", ++count);
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
     * @param steamAssetAlls
     * @return
     */
    public List<SteamAsset> postBulks(List<SteamAsset> steamAssetAlls) {
        //分配获取painwear
        List<SteamAsset> postBuilPara = new ArrayList<>();
        int count = 0;
        for (SteamAsset asset : steamAssetAlls) {
            postBuilPara.add(asset);
            if (postBuilPara.size() > 48) {
                log.info("第:{}页steam数据和buff数据进行关联", ++count);
                postBulk(postBuilPara);
                postBuilPara.clear();
            }
        }
        postBulk(postBuilPara);
        return steamAssetAlls;
    }

    /**
     * 获取磨损数据数据
     *
     * @param postBuilPara
     * @return
     */
    public List<SteamAsset> postBulk(List<SteamAsset> postBuilPara) {
        List<Map<String, String>> links = new ArrayList<>();
        //构建参数
        for (SteamAsset steamAsset : postBuilPara) {
            Map<String, String> hashMap = new HashMap();
            hashMap.put("link", steamAsset.getLink());
            links.add(hashMap);
        }
        // a 和磨损度的映射
        Map<String, Double> listIdAndFloatValueMap = getListIdAndWearMap(links);
        for (Map.Entry<String, Double> entry : listIdAndFloatValueMap.entrySet()) {
            String linkKey = entry.getKey();
            String key = "A" + linkKey + "D";
            for (SteamAsset steamAsset : postBuilPara) {
                if (steamAsset.getLink().contains(key)) {
                    steamAsset.setPainwear(String.valueOf(entry.getValue()));
                }
            }
        }
        return postBuilPara;
    }


    /**
     * 根据url获取磨损度
     *
     * @param links
     * @return
     */
    public Map<String, Double> getListIdAndWearMap(List<Map<String, String>> links) {
        String url = "http://45.144.136.173:8006/bulk";
        HashMap hashMap = new HashMap();
        hashMap.put("links", links);
        String reponse = "";
        for (int i = 0; i < 3; i++) {
            try {
                reponse = HttpClientUtils.sendPost(url, JSONObject.toJSONString(hashMap), new HashMap<>());
                if (StrUtil.isEmpty(reponse)) {
                    SleepUtil.sleep(3 * 1000);
                    if (i >= 2) {
                        return new HashMap<>();
                    }
                    continue;
                }
                break;
            } catch (Exception e) {
                log.error("获取磨损度失败,重新尝试发送【{}】请求,失败信息为:", (i + 1), e);
                if (i >= 2) {
                    log.error("获取磨损度失败，参数为{}", JSONObject.toJSONString(hashMap));
                    return new HashMap<>();
                }
                SleepUtil.sleep(3 * 1000);
            }
        }

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
