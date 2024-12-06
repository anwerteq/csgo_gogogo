package com.chenerzhu.crawler.proxy.steam.service;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.config.BatchInsertService;
import com.chenerzhu.crawler.proxy.csgo.entity.ItemGoods;
import com.chenerzhu.crawler.proxy.csgo.repository.IItemGoodsRepository;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Assets;
import com.chenerzhu.crawler.proxy.steam.entity.Descriptions;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.InventoryRootBean;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.repository.DescriptionsRepository;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.util.MtdtDbUtil;
import com.chenerzhu.crawler.proxy.util.SteamTheadeUtil;
import com.chenerzhu.crawler.proxy.util.TempTableSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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

    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    MtdtDbUtil mtdtDbUtil;

    @Autowired
    TempTableSaveService tempTableSaveService;

    @Autowired
    IItemGoodsRepository iItemGoodsRepository;


    /**
     * 获取steam库存
     */
    private InventoryRootBean getSteamInventory() {
        SleepUtil.sleep();
        String steamID = SteamTheadeUtil.getThreadSteamUserDate().getSession().getSteamID();
        String url = "https://steamcommunity.com/inventory/" + steamID  + "/730/2?l=schinese&count=2000&market=1";
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        if (StrUtil.isEmpty(resStr)) {
            log.error("获取steam库存失败");
            throw new ArithmeticException("获取steam库存失败");
        }
        InventoryRootBean inventoryRootBean = JSONObject.parseObject(resStr, InventoryRootBean.class);
        return inventoryRootBean;
    }

    public void refreshSteamInventory() {
        InventoryRootBean steamInventory = getSteamInventory();
        Map<String, List<Assets>>  keyAndAssets = steamInventory.getAssets().stream().collect(Collectors.groupingBy(o1-> o1.getClassid() + o1.getInstanceid()));
        Map<String, Descriptions> keyAndDescriptions = steamInventory.getDescriptions().stream().collect(Collectors.toMap(o1 -> o1.getClassid() + o1.getInstanceid(), o2 -> o2, (o1, o2) -> o1, HashMap::new));
        List<Descriptions> descriptionsList = new ArrayList<>();
        Map<String, Double> hashNameAndPrice = iItemGoodsRepository.findAll().stream().collect(Collectors.toMap(ItemGoods::getMarketHashName, ItemGoods::getSell_min_price));
        for (Map.Entry<String, List<Assets>> assetsEntry : keyAndAssets.entrySet()) {
            List<Assets> assetss = assetsEntry.getValue();
            for (Assets assets : assetss) {
                Descriptions descriptions = keyAndDescriptions.get(assetsEntry.getKey());
                Descriptions descriptionsNew = JSONObject.parseObject(JSONObject.toJSONString(descriptions), Descriptions.class);
                descriptionsNew.setAssetid(assets.getAssetid());
                descriptionsNew.setAmount(Integer.parseInt(assets.getAmount()));
                descriptionsNew.setCdkey_id(descriptionsNew.assetidClassidInstanceid());
                descriptionsNew.setNumber_name(SteamTheadeUtil.getThreadSteamUserDate().getAccount_name());
                Double price = hashNameAndPrice.get(descriptionsNew.getMarket_hash_name());
                descriptionsNew.setBuff_min_price(price);
                descriptionsList.add(descriptionsNew);
            }
        }
        List<String> collect = descriptionsList.stream().map(Descriptions::getCdkey_id).collect(Collectors.toList());
        System.out.println(new Date());
        mtdtDbUtil.execSql("delete from steam_descriptions where  cdkey_id  ",collect);
         System.out.println(new Date());
        tempTableSaveService.batchSave(descriptionsList,Descriptions.class);
        System.out.println(new Date());

        System.out.println("123123");
    }

    @Transactional
    public void batchSave(List<?> entities, int batchSize) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.persist(entities.get(i));

            // 每到达批次大小时，刷新并清理持久化上下文
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        // 提交剩余数据
        entityManager.flush();
        entityManager.clear();
    }
}
