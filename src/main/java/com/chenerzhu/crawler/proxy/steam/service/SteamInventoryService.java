package com.chenerzhu.crawler.proxy.steam.service;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.csgo.entity.BuffCostEntity;
import com.chenerzhu.crawler.proxy.csgo.entity.ItemGoods;
import com.chenerzhu.crawler.proxy.csgo.repository.BuffCostRepository;
import com.chenerzhu.crawler.proxy.csgo.repository.IItemGoodsRepository;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Assets;
import com.chenerzhu.crawler.proxy.steam.entity.CZ75Item;
import com.chenerzhu.crawler.proxy.steam.entity.Descriptions;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.InventoryRootBean;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.repository.CZ75ItemRepository;
import com.chenerzhu.crawler.proxy.steam.repository.DescriptionsRepository;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.util.SteamTheadeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * steam历史记录
 */
@Service
@Slf4j
public class SteamInventoryService {

    @Autowired
    SteamBuyItemService steamBuyItemService;

    @Autowired
    DescriptionsRepository descriptionsRepository;

    @Autowired
    BuffCostRepository buffCostRepository;



    @Autowired
    IItemGoodsRepository iItemGoodsRepository;

    @Autowired
    CZ75ItemRepository cz75ItemRepository;

    /**
     * 获取steam库存
     */
    private InventoryRootBean getSteamInventory() {
        SleepUtil.sleep();
        String steamID = SteamTheadeUtil.getThreadSteamUserDate().getSession().getSteamID();
        String url = "https://steamcommunity.com/inventory/" + steamID  + "/730/2?l=schinese&count=3000&market=1";
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        if (StrUtil.isEmpty(resStr)) {
            log.error("获取steam库存失败");
            throw new ArithmeticException("获取steam库存失败");
        }
        InventoryRootBean inventoryRootBean = JSONObject.parseObject(resStr, InventoryRootBean.class);
        return inventoryRootBean;
    }

    @Transactional
    public void refreshSteamInventory() {
        log.info("开始拉取steam 接口中的的库存信息");
        InventoryRootBean steamInventory = getSteamInventory();
        Map<String, List<Assets>>  keyAndAssets = steamInventory.getAssets().stream().collect(Collectors.groupingBy(o1-> o1.getClassid() +"-"+ o1.getInstanceid()));
        Map<String, Descriptions> keyAndDescriptions = steamInventory.getDescriptions().stream().collect(Collectors.toMap(o1 -> o1.getClassid() +"-"+ o1.getInstanceid(), o2 -> o2, (o1, o2) -> o1, HashMap::new));
        List<Descriptions> descriptionsList = new ArrayList<>();
        Map<String, ItemGoods> hashNameAndItemGoods = iItemGoodsRepository.findAll().stream().collect(Collectors.toMap(ItemGoods::getMarketHashName, e -> e));
        String steamID = SteamTheadeUtil.getThreadSteamUserDate().getSession().getSteamID();
        for (Map.Entry<String, List<Assets>> assetsEntry : keyAndAssets.entrySet()) {
            List<Assets> assetss = assetsEntry.getValue();
            for (Assets assets : assetss) {
                Descriptions descriptions = keyAndDescriptions.get(assetsEntry.getKey());
                Descriptions descriptionsNew = JSONObject.parseObject(JSONObject.toJSONString(descriptions), Descriptions.class);
                descriptionsNew.setAssetid(assets.getAssetid());
                descriptionsNew.setAmount(Integer.parseInt(assets.getAmount()));
                descriptionsNew.setNumber_name(SteamTheadeUtil.getThreadSteamUserDate().getAccount_name());
                descriptionsNew.setSteamId(steamID);
                ItemGoods itemGoods = hashNameAndItemGoods.get(descriptionsNew.getMarket_hash_name());
                if (itemGoods == null){
                    continue;
                }
                descriptionsNew.setBuff_min_price( itemGoods.getSell_min_price());
                descriptionsNew.setSteam_price(itemGoods.getSteam_price());
                descriptionsNew.refreashCdkey_id();
                descriptionsNew.setCreate_date(LocalDateTime.now());
                descriptionsNew.refreshSteamInventoryMarkId();
                descriptionsList.add(descriptionsNew);
            }
        }
        //设置购入价格
        List<String> collect = descriptionsList.stream().map(Descriptions::getSteamInventoryMarkId).collect(Collectors.toList());
        //获取steam市场交易记录
        List<CZ75Item> buyCZ75Item = cz75ItemRepository.findBySteamInventoryMarkIdInAndTheTypeOfTransaction(collect, "买");
        buyCZ75Item.sort((o1,o2)-> o2.getMemo().compareTo(o1.getMemo()));
        Map<String, Double> steamInventoryMarkIdAndPrice =buyCZ75Item.stream()
                .collect(Collectors.toMap(CZ75Item::getSteamInventoryMarkId,CZ75Item::getUsd,(o1,o2)->o1));

        //获取buff的购买价格
        List<String> cdkeyIds = descriptionsList.stream().map(Descriptions::getCdkey_id).collect(Collectors.toList());
        List<BuffCostEntity> byCdkeyIdIn = buffCostRepository.findByCdkeyIdIn(cdkeyIds);
        HashMap<String, Double> cdKeyIdAndPriva = byCdkeyIdIn.stream().collect(Collectors.toMap(BuffCostEntity::getCdkeyId, BuffCostEntity::getBuff_cost, (e1, e2) -> e1, HashMap::new));

        for (Descriptions descriptions : descriptionsList) {
            Double orDefault = steamInventoryMarkIdAndPrice.getOrDefault(descriptions.getSteamInventoryMarkId(), null);
            descriptions.setBuy_price(orDefault);
            if (orDefault != null){
                descriptions.setBuy_type("平台:steam");
            }else {
                Double orDefault1 = cdKeyIdAndPriva.getOrDefault(descriptions.getCdkey_id(), null);
                descriptions.setBuy_price(orDefault1);
                if (orDefault1 != null){
                    descriptions.setBuy_type("平台:buff");
                }
            }
        }
        //获取buff交易记录
        List<String> ids = descriptionsList.stream().map(Descriptions::getCdkey_id).collect(Collectors.toList());
        List<BuffCostEntity> allById = buffCostRepository.findAllById(ids);
        allById.stream().collect(Collectors.toMap(BuffCostEntity::getCdkeyId, buffCostEntity -> buffCostEntity));
        descriptionsRepository.deleteBySteamId(steamID);
        descriptionsRepository.saveAll(descriptionsList);
        log.info("刷新steam库存信息完成");
    }


}
