package com.chenerzhu.crawler.proxy.buff.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.applicationRunners.BuffApplicationRunner;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.steam.entity.Descriptions;
import com.chenerzhu.crawler.proxy.steam.repository.DescriptionsRepository;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 设置buff购买成本
 */
@Service
@Slf4j
public class BuffSetMemoService {

    @Autowired
    DescriptionsRepository descriptionsRepository;



    /**
     * 设置成本的主要逻辑
     */
    public void assetRemarkChange() {
        log.info("开始更新buff的备注信息");
        String steamId = BuffApplicationRunner.buffUserDataThreadLocal.get().getSteamId();
        List<Descriptions> allByBySteamId = descriptionsRepository.findAllBySteamId(steamId);
        List<Descriptions> descriptionsList = allByBySteamId;
                //allByBySteamId.stream().filter(descriptions -> ObjectUtil.isNotNull(descriptions.getBuy_price())).collect(Collectors.toList());
        List<Descriptions> childList = new ArrayList<>();
        for (Descriptions descriptions : descriptionsList) {
            childList.add(descriptions);
            if (childList.size() >= 40){
                remarkChange(childList);
                childList.clear();
            }
        }
        remarkChange(childList);
        log.info("更新buff的备注信息结束");
    }



    /**
     * 批量设置备注信息
     * @param descriptionsList
     */
    public void remarkChange(List<Descriptions> descriptionsList) {
        if (descriptionsList.isEmpty()){
            return;
        }
        SleepUtil.sleep(10 * 1000);
        String url = "https://buff.163.com/api/market/steam_asset_remark/change";
        Map<String, Object> dataMap = buildRemarkParamer(descriptionsList);
        Map<String, String> headerMap1 = new HashMap<>();
        headerMap1.put("Cookie", BuffConfig.getCookie());
        headerMap1.put("Referer", "https://buff.163.com/market/steam_inventory?game=csgo");
        headerMap1.put("X-Csrftoken", BuffConfig.getCookieOnlyKey("csrf_token"));
        headerMap1.put("Origin", "https://buff.163.com");
        headerMap1.put("Content-Type", "application/json");
        headerMap1.put("X-Requested-With", "XMLHttpRequest");
        String reponse = HttpClientUtils.sendPost(url, JSONObject.toJSONString(dataMap), headerMap1);
        JSONObject jsonObject = JSONObject.parseObject(reponse);
        String code = jsonObject.getString("code");
        if (StrUtil.isEmpty(code)) {
            log.error("饰品备注失败:错误信息为:{}", reponse);
            return;
        }
        log.info("饰品共计：{},备注成功",descriptionsList.size());
    }

    /**
     * 构建修改成本价参数
     *
     * @return
     */
    public Map<String, Object> buildRemarkParamer(List<Descriptions> descriptionsList) {
        List<Map<String, Object>> assets = new ArrayList<>();
        for (Descriptions descriptions : descriptionsList) {
            Map<String, Object> hashMap = new HashMap();
           if (descriptions.getBuy_price() != null){
               hashMap.put("remark", "成本:" + descriptions.getBuy_price() + "元");
           }else {
               hashMap.put("remark", "");
           }

            hashMap.put("assetid", descriptions.getAssetid());
            assets.add(hashMap);
        }

        Map<String, Object> hashMap = new HashMap();
        hashMap.put("game", "csgo");
        hashMap.put("assets", assets);
        return hashMap;
    }





}
