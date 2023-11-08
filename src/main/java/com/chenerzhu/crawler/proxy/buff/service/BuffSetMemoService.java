package com.chenerzhu.crawler.proxy.buff.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.applicationRunners.BuffApplicationRunner;
import com.chenerzhu.crawler.proxy.applicationRunners.SteamApplicationRunner;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.AssetExtra;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.Items;
import com.chenerzhu.crawler.proxy.csgofloat.CsgoFloatService;
import com.chenerzhu.crawler.proxy.steam.service.SteamMyhistoryService;
import com.chenerzhu.crawler.proxy.steam.service.steamrenderhistory.SteamAsset;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 设置buff购买成本
 */
@Service
@Slf4j
public class BuffSetMemoService {

    @Value("${cark_cost}")
    Double cark_cost;

    @Autowired
    SteamInventorySerivce steamInventorySerivce;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SteamMyhistoryService steamMyhistoryService;

    @Autowired
    CsgoFloatService csgoFloatService;

    /**
     * 设置成本的主要逻辑
     */
    public void assetRemarkChange() {
        if (!SteamApplicationRunner.checkHasSteamCookie()) {
            BuffUserData buffUserData = BuffApplicationRunner.buffUserDataThreadLocal.get();
            log.info("steamId:{}未加载", buffUserData.getSteamId());
            SleepUtil.sleep(4 * 1000);
            return;
        }
        List<Items> allStatusInventory = getAllStatusInventory();
//        allStatusInventory = filterRemark(allStatusInventory);
        if (allStatusInventory.isEmpty()) {
            return;
        }

        List<SteamAsset> steamAssetAlls = new ArrayList<>();
        int page_index = 1;
        while (true) {
            //饰品购买的价格
            List<SteamAsset> steamAssets = steamMyhistoryService.marketMyhistorys(page_index++);
            if (steamAssets == null) {
                break;
            }
            SleepUtil.sleep(5 * 1000);
            steamAssetAlls.addAll(steamAssets);
        }
        //设置buff的磨损
        allStatusInventory = csgoFloatService.postBuffBulks(allStatusInventory);
        //获取磨损度和价格的映射
        steamAssetAlls = csgoFloatService.postBulks(steamAssetAlls);
        remarkChanges(allStatusInventory, steamAssetAlls);
    }


    /**
     * 过滤已经有备注的饰品
     *
     * @param items
     * @return
     */
    public List<Items> filterRemark(List<Items> items) {
        List<Items> collect = items.stream().filter(items1 -> {
            AssetExtra asset_extra = items1.getAsset_extra();
            if (ObjectUtil.isNull(asset_extra)) {
                return true;
            }
            String remark = items1.getAsset_extra().getRemark();
            return !StrUtil.isNotEmpty(remark) || !remark.contains("成本:");
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 获取steam的饰品数据
     *
     * @return
     */
    public Map<String, String> getSteamWearMap(Map<String, String> itemOnlyKeyAndPriceMap) {

        return null;
    }


    /**
     * 获取库存的全部信息
     *
     * @return
     */
    public List<Items> getAllStatusInventory() {
        List<Items> list = new ArrayList<>();
        int pageIndex = 1;
        while (true) {
            List<Items> list1 = steamInventorySerivce.steamAllStatusInventory(pageIndex++);
            list.addAll(list1);
            if (list1.isEmpty() || list1.size() < 500) {
                break;
            }

        }
        return list;
    }

    /**
     * 批量设置备注信息
     *
     * @param items
     * @param steamAssetAlls
     */
    public void remarkChanges(List<Items> items, List<SteamAsset> steamAssetAlls) {
        List<Items> itemsChanges = new ArrayList();
        for (Items item : items) {
            itemsChanges.add(item);
            if (itemsChanges.size() > 40) {
                remarkChange(itemsChanges, steamAssetAlls);
                itemsChanges.clear();
                SleepUtil.sleep(5 * 1000);
            }
        }
        remarkChange(itemsChanges, steamAssetAlls);
    }

    /**
     * 批量设置备注信息
     *
     * @param items
     * @param steamAssetAlls
     */
    public void remarkChange(List<Items> items, List<SteamAsset> steamAssetAlls) {
        String url = "https://buff.163.com/api/market/steam_asset_remark/change";
        Map<String, Object> dataMap = buildRemarkParamer(items, steamAssetAlls);
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
        log.info("饰品备注成功");
    }

    /**
     * 构建修改成本价参数
     *
     * @return
     */
    public Map<String, Object> buildRemarkParamer(List<Items> items, List<SteamAsset> steamAssetAlls) {
        List<Map<String, Object>> assets = new ArrayList<>();
        //构建aeestId和memo的映射
        Map<Long, String> assetIdAndCost = buildBuffAssetAndPriceParamer(items, steamAssetAlls);
        //buff aeestId和memo的映射
        for (Map.Entry<Long, String> entry : assetIdAndCost.entrySet()) {
            Map<String, Object> hashMap = new HashMap();
            hashMap.put("remark", "成本:" + getCostRmb(entry.getValue()) + "元");
            hashMap.put("assetid", entry.getKey());
            assets.add(hashMap);
        }
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("game", "csgo");
        hashMap.put("assets", assets);
        return hashMap;
    }

    /**
     * 获取buff assetis和美金成本的映射
     *
     * @param items
     * @param steamAssetAlls
     * @return
     */
    public Map<Long, String> buildBuffAssetAndPriceParamer(List<Items> items, List<SteamAsset> steamAssetAlls) {
        Map<String, List<SteamAsset>> steamOnlykeyMap = steamAssetAlls.stream().collect(Collectors.groupingBy(SteamAsset::getInstanceidAndPainwear));
        //因为buff识别的磨损度和steam识别的有差异,最大缩小五位
        int beforeCount = steamOnlykeyMap.keySet().size();
        int reducedValue = 0;
        //同一个饰品的磨损度，应该不会差太多
        for (int i = 0; i < 2; i++) {
            final int tempI = i;
            Set<String> temp = steamOnlykeyMap.keySet().stream().map(str -> str.substring(0, str.length() - tempI))
                    //防止磨损度为 0.25的情况
                    .filter(str -> str.length() > 3).collect(Collectors.toSet());
            if (beforeCount == temp.size()) {
                reducedValue = i;
            } else {
                break;
            }
        }
        //key是和buff和steam饰品的关联字段
        Map<String, List<Items>> buffOnlyKeyMap = items.stream().collect(Collectors.groupingBy(Items::getInstanceidAndPainwear));
        //assetId和美元金额的映射
        Map<Long, String> assetIdAndCost = new HashMap<>();
        for (Map.Entry<String, List<Items>> buffEntry : buffOnlyKeyMap.entrySet()) {
            //获取buff产生的关联id
            String buffOnlyId = getAssociation(buffEntry.getKey(), reducedValue);
            for (Map.Entry<String, List<SteamAsset>> steamEntry : steamOnlykeyMap.entrySet()) {
                //获取steam产生的关联id
                String steamOnlyId = getAssociation(steamEntry.getKey(), reducedValue);
                if (buffOnlyId.equals(steamOnlyId) || buffOnlyId.contains(steamOnlyId) || steamOnlyId.contains(buffOnlyId)) {
                    String assetid = buffEntry.getValue().get(0).getAsset_info().getAssetid();
                    String price = steamEntry.getValue().get(0).getPrice();
                    assetIdAndCost.put(Long.valueOf(assetid), price);
                    break;
                }
            }
        }
        return assetIdAndCost;
    }


    /**
     * 获取新的关联字段
     *
     * @return
     */
    public String getAssociation(String key, int reducedValue) {
        String newKey = key.substring(0, key.length() - reducedValue);
        return newKey;
    }


    /**
     * 转化成rmb
     *
     * @param dollar
     * @return
     */
    public Double getCostRmb(String dollar) {
        Double dollarD = Double.valueOf(dollar);
        Double rmb = dollarD * cark_cost;
        return Math.round(rmb * 1000.0) / 1000.0;
    }


}
