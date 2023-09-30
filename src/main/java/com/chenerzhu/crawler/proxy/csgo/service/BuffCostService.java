package com.chenerzhu.crawler.proxy.csgo.service;


import cn.hutool.core.util.ObjectUtil;
import com.chenerzhu.crawler.proxy.buff.entity.BuffCostEntity;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.BuffBuyItems;
import com.chenerzhu.crawler.proxy.csgo.repository.BuffCostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;


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
        buffCostEntity.setCreate_time(new Date());
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
     *
     * @param assetid
     * @param classid
     * @return
     */
    public BuffCostEntity getLowCostCent(String assetid, String classid, String market_hash_name, int afterTaxCentMoney) {
        BuffCostEntity buffCostEntity = buffCostRepository.selectOne(Long.valueOf(assetid), Long.valueOf(classid));
        if (ObjectUtil.isNull(buffCostEntity)) {
            //steam的库存信息没有和buff购买信息匹配上
            buffCostEntity = buffCostRepository.selectOne(market_hash_name);
        }
        Boolean flag =false;
        if (ObjectUtil.isNull(buffCostEntity)) {
            buffCostEntity = buffCostRepository.selectOneNotMate(market_hash_name);
            flag = true;
        }
        if (ObjectUtil.isNull(buffCostEntity)) {
            return null;
        }
        //购买成本   7 *x * 0.85  = y (金额)   9/y > 0.8
        //   cost/x*7 < 0.8      cost /5.6 = x
        double buff_cost = buffCostEntity.getBuff_cost();
        //计算公式：人民币换成美金，然后除想要的利率折扣
        //如：本金56rmb,正常换美金 56/7 = 8美金 ，通过计算56/7/0.8 = 10美金
        double lowCostDollar = buff_cost / 7 / 0.78;
        //转换成美分
        int lowCostCent = Double.valueOf(lowCostDollar * 100).intValue() + 1;

        //获取最大的销售金额
        int steamAfterTaxPrice = Math.max(afterTaxCentMoney, lowCostCent);
        //没有，匹配过，进行匹配
        if (buffCostEntity.getIs_mate() == 0) {
            buffCostEntity.setAssetid(Long.valueOf(assetid));
            buffCostEntity.setClassid(Long.valueOf(classid));
            buffCostEntity.setIs_mate(1);
        }
        buffCostEntity.setReturned_money(steamAfterTaxPrice * 7);
        buffCostEntity.setUpdate_time(new Date());
        buffCostEntity.setHash_name(market_hash_name);
        if (flag){
            //没有找到匹配的数据，用相同产品的最大价格去计算
            return buffCostEntity;
        }
        buffCostRepository.save(buffCostEntity);
        return buffCostEntity;
    }

    /**
     * 根据订单获取的数据去更新buff的购买记录
     * @param arrayList
     */
    public void byOrderHistorySave(List<BuffCostEntity> arrayList){
        for (BuffCostEntity costEntity : arrayList) {
            int i = buffCostRepository.exitClassAndAssetid(costEntity.getAssetid(), costEntity.getClassid());
            if (i > 0){
                //这条记录生成过
                continue;
            }
            costEntity.setBuy_status(2);
            costEntity.setCostId(UUID.randomUUID().toString());
            buffCostRepository.save(costEntity);
        }
    }


    public static void main(String[] args) {


        double lowCostDollar = 1.71 / 7 / 0.8;
        //转换成美分
        int lowCostCent = Double.valueOf(lowCostDollar * 100).intValue() + 1;
        System.out.println(lowCostCent);
    }
}
