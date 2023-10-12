package com.chenerzhu.crawler.proxy.steam.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.entity.BuffCostEntity;
import com.chenerzhu.crawler.proxy.csgo.repository.BuffCostRepository;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Assets;
import com.chenerzhu.crawler.proxy.steam.CreatebuyorderEntity;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.entity.SteamCostEntity;
import com.chenerzhu.crawler.proxy.steam.repository.SteamCostRepository;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.*;

/**
 * steam购买商品逻辑
 */
@Service
@Slf4j
public class SteamBuyItemService {


    @Autowired
    SteamCostRepository steamCostRepository;

    @Autowired
    BuffCostRepository buffCostRepository;

    /**
     * 提交steam订单
     *
     * @param price_total：美分
     * @param market_hash_name
     */
    public void createbuyorder(int price_total, String market_hash_name, int quantity, String name) {
        CreatebuyorderEntity createbuyorderEntity = new CreatebuyorderEntity();
        createbuyorderEntity.setMarket_hash_name(market_hash_name);
        createbuyorderEntity.setQuantity(String.valueOf(quantity));
        createbuyorderEntity.setPrice_total(String.valueOf(price_total * Integer.parseInt(createbuyorderEntity.getQuantity())));
        createbuyorderEntity.setSessionid(SteamConfig.getCookieOnlyKey("sessionid"));
        Map<String, String> saleHeader = SteamConfig.getBuyHeader();
        saleHeader.put("Referer", "https://steamcommunity.com/market/listings/730/" + URLEncoder.encode(market_hash_name));
        HashMap hashMap = JSONObject.parseObject(JSONObject.toJSONString(createbuyorderEntity), HashMap.class);
        String url = "https://steamcommunity.com/market/createbuyorder";
        // post ,x-www
        String responseStr = HttpClientUtils.sendPostForm(url, "", saleHeader, hashMap);
        JSONObject jsonObject1 = JSONObject.parseObject(responseStr);
        Object success1 = jsonObject1.get("success");
        if (ObjectUtil.isNull(success1)) {
            log.error("商品:{},发送求购steam请求失败,steam返回的异常信息为:{}", name, jsonObject1);
            return;
        }
        if ("1".equals(success1.toString())) {
            log.info("商品:{},发起steam求购成功", name);
        } else {
            log.info("商品:{},发送求购订单失败,steam返回的信息为:{}" + name, jsonObject1);
        }
    }


    /**
     * 保存steam商品购买信息
     *
     * @param buyOrderEntity
     */
    public void saveSteamCostEntity(CreatebuyorderEntity buyOrderEntity) {
        ArrayList<SteamCostEntity> arrayList = new ArrayList();
        int quantity = Integer.parseInt(buyOrderEntity.getQuantity());
        for (int i = 0; i < quantity; i++) {
            SteamCostEntity steamCostEntity = new SteamCostEntity();
            steamCostEntity.setCostId(UUID.randomUUID().toString());
            steamCostEntity.setSteam_cost(Double.valueOf(buyOrderEntity.getPrice_total()));
            steamCostEntity.setHash_name(buyOrderEntity.getMarket_hash_name());
            steamCostEntity.setCreate_time(new Date());
            steamCostEntity.setBuy_status(0);
            arrayList.add(steamCostEntity);
        }
        steamCostRepository.saveAll(arrayList);
    }


    /**
     * steam上架，更新team购买商品的记录
     *
     * @param assets
     * @param steamCostEntity
     * @param name
     */
    public void updateSteamCostEntity(Assets assets, SteamCostEntity steamCostEntity, String name) {
        steamCostEntity.setUpdate_time(new Date());
        steamCostEntity.setBuy_status(1);
        steamCostEntity.setClassid(assets.getClassid());
        steamCostEntity.setAssetid(assets.getAssetid());
        steamCostEntity.setName(name);
        steamCostRepository.save(steamCostEntity);
    }


    /**
     * 在buff上架的物品，进行销售价格登记
     *
     * @param createAssets
     */
    public void afterTopBuffUpdateCost(List<Assets> createAssets) {

        List<SteamCostEntity> steamCostEntityList = new ArrayList<>();
        for (Assets assets : createAssets) {
            SteamCostEntity steamCostEntity = steamCostRepository.selectByAssetId(assets.getAssetid(), assets.getClassid());
            if (ObjectUtil.isNull(steamCostEntity)) {
                continue;
            }
            steamCostEntity.setReturned_money(Integer.parseInt(assets.getIncome()) * 100 / 7);
            steamCostEntity.setUpdate_time(new Date());
            steamCostEntity.setBuy_status(2);
            steamCostEntityList.add(steamCostEntity);
        }
        steamCostRepository.saveAll(steamCostEntityList);
    }


    /**
     * 返回人民币单位：元
     *
     * @param assid
     * @param classId
     * @return
     */
    public Double getBuySteamPrice(String assid, String classId) {
        SteamCostEntity steamCostEntity = steamCostRepository.selectByAssetId(assid, classId);
        if (ObjectUtil.isNull(steamCostEntity)) {
            return 0.0;
        }
        double steamCost_RMB = steamCostEntity.getSteam_cost() * 7 / 100;
        return steamCost_RMB * 1.1;

    }

    /**
     * 更新销售金额
     */
    public void saveForsellPrice(SteamCostEntity costEntity) {

        SteamCostEntity steamCostEntity = steamCostRepository.selectByAssetId(costEntity.getAssetid(), costEntity.getClassid());
        //steam求购到，又售卖出去的
        if (ObjectUtil.isNotNull(steamCostEntity)) {
            //更新销售金额
            steamCostEntity.setReturned_money(costEntity.getReturned_money());
            steamCostRepository.save(steamCostEntity);
            return;
        }
        //steam_cost没有查询到 从buff中查询
        BuffCostEntity buffCostEntity = buffCostRepository.selectOne(Long.valueOf(costEntity.getAssetid()), Long.valueOf(costEntity.getClassid()));
        if (ObjectUtil.isNotNull(buffCostEntity)) {
            buffCostEntity.setReturned_money(costEntity.getReturned_money() * 7);
            buffCostEntity.setIs_mate(1);
            buffCostEntity.setBuy_status(3);
            buffCostEntity.setHash_name(costEntity.getHash_name());
            buffCostEntity.setUpdate_time(new Date());
            buffCostRepository.save(buffCostEntity);
        }

    }


    /**
     * 更新购买金额（从历史记录里面获获取steam求购到的订单信息）
     */
    public void saveForCostPrice(SteamCostEntity costEntity) {
        SteamCostEntity steamCostEntity = steamCostRepository.selectByAssetIdNotBuyStatus(costEntity.getAssetid(), costEntity.getClassid());
        //steam求购的时候，保存了信息
        if (ObjectUtil.isNotNull(steamCostEntity)) {
            //更新在steam购买时记录的信息
            steamCostEntity.setSteam_cost(costEntity.getSteam_cost());
            steamCostEntity.setCreate_time(new Date());
            steamCostEntity.setUpdate_time(new Date());
            steamCostRepository.save(steamCostEntity);
        } else {
            //steam下订单的时候没有记录
            costEntity.setCostId(UUID.randomUUID().toString());
            costEntity.setCreate_time(new Date());

            steamCostRepository.save(costEntity);
        }

    }
}
