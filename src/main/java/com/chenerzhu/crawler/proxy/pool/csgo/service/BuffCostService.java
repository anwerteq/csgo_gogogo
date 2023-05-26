package com.chenerzhu.crawler.proxy.pool.csgo.service;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.ExecutorUtil;
import com.chenerzhu.crawler.proxy.buff.entity.BuffCostEntity;
import com.chenerzhu.crawler.proxy.buff.service.ProfitService;
import com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity.*;
import com.chenerzhu.crawler.proxy.pool.csgo.buyentity.PayBillRepData;
import com.chenerzhu.crawler.proxy.pool.csgo.buyentity.PayBillRepRoot;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffCreateBillRoot;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellSteamProfitEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.BuffCostRepository;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.SellSteamProfitRepository;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;


/**
 * buff商品购买利润记录
 */
@Service
@Slf4j
public class BuffCostService {

    @Autowired
    BuffCostRepository buffCostRepository;

    /**
     * 记录buff的购买商品
     */
    public BuffCostEntity createMarkCost(BuffBuyItems buyItem) {
        BuffCostEntity buffCostEntity = new BuffCostEntity();
        buffCostEntity.setCostId(UUID.randomUUID().toString());
        buffCostEntity.setBuff_cost(Double.valueOf(buyItem.getPrice()));
        buffCostEntity.setAssetid(Long.valueOf(buyItem.getAsset_info().getAssetid()));
        buffCostEntity.setClassid(Long.valueOf(buyItem.getAsset_info().getClassid()));
        buffCostEntity.setName(buyItem.getName());
        buffCostEntity.setHash_name(buyItem.getHash_name());
        BuffCostEntity save = buffCostRepository.save(buffCostEntity);
        return save;
    }

    public BuffCostEntity updateCostStatus(BuffCostEntity buffCostEntity, int status) {
        buffCostEntity.setBuy_status(status);
        BuffCostEntity save = buffCostRepository.save(buffCostEntity);
        return save;
    }

    /**
     * 获取商品最低税后美分
     * @param assetid
     * @param classid
     * @return
     */
    public int getLowCostCent(String assetid, String classid) {
        BuffCostEntity buffCostEntity = buffCostRepository.selectOne(Long.valueOf(assetid), Long.valueOf(classid));
        if (ObjectUtil.isNull(buffCostEntity)) {
            return 0;
        }
        //购买成本   7 *x * 0.85  = y (金额)   9/y > 0.8
        //   cost/x*7 < 0.8      cost /5.6 = x
        double buff_cost = buffCostEntity.getBuff_cost();
        //计算公式：人民币换成美金，然后除想要的利率折扣
        //如：本金56rmb,正常换美金 56/7 = 8美金 ，通过计算56/7/0.8 = 10美金
        double lowCostDollar = buff_cost / 7 / 0.8;
        //转换成美分
        int lowCostCent = Double.valueOf(lowCostDollar * 100).intValue();
        return lowCostCent;
    }
}
