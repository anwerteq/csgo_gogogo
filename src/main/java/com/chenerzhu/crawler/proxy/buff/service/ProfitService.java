package com.chenerzhu.crawler.proxy.buff.service;

import com.chenerzhu.crawler.proxy.pool.csgo.entity.ItemGoods;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellBuffProfitEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellSteamProfitEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.SellBuffProfitRepository;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.SellSteamProfitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * buff利率服务
 */
@Service
public class ProfitService {
    @Autowired

    SellBuffProfitRepository sellBuffProfitRepository;


    @Autowired
    SellSteamProfitRepository sellSteamProfitRepository;

    /**
     * 保存推荐在steam购买的记录
     */
    @Async
    public void saveSellBuffProfitEntity(ItemGoods itemGoods) {
        SellBuffProfitEntity sellBuffProfitEntity = new SellBuffProfitEntity();
        sellBuffProfitEntity.setItem_id(itemGoods.getId());
        sellBuffProfitEntity.setName(itemGoods.getName());
        sellBuffProfitEntity.setSteam_price_cny(Double.parseDouble(itemGoods.getGoods_info().getSteam_price_cny()));
        ;
        //购买成本
        double profit = sellBuffProfitEntity.getSteam_price_cny() * 1.15 * 0.85;
        sellBuffProfitEntity.setIn_fact_steam_price_cny(Double.valueOf(String.format("%.3f", profit)));
        sellBuffProfitEntity.setSell_min_price(Double.valueOf(itemGoods.getSell_min_price()));
        sellBuffProfitEntity.setQuick_price(Double.valueOf(itemGoods.getQuick_price()));
        sellBuffProfitEntity.setSell_num(String.valueOf(itemGoods.getSell_num()));
        double interest =sellBuffProfitEntity.getSell_min_price() - sellBuffProfitEntity.getIn_fact_steam_price_cny();
        double interest_rate = (interest / sellBuffProfitEntity.getIn_fact_steam_price_cny() * 100);
        sellBuffProfitEntity.setInterest_rate(String.format("%.3f", interest_rate));
        sellBuffProfitEntity.setUp_date(new Date());
        sellBuffProfitEntity.setMarket_hash_name(itemGoods.getMarket_hash_name());
        Boolean flag = false;
        if (3.0f < interest_rate) {
            //在buff售卖，利率超过3%
            flag = true;
        }
        if (flag) {
            sellBuffProfitRepository.save(sellBuffProfitEntity);
        }
    }


    /**
     * 保存在steam售卖的购买记录
     */
    @Async
    public void saveSellSteamProfit(ItemGoods itemGoods) {
        SellSteamProfitEntity item = checkBuyBuffItem(itemGoods);
        if (item != null){
            sellSteamProfitRepository.save(item);
        }
    }


    /**
     *
     * @param itemGoods
     * @return 返回null 不推荐购买
     */
    public SellSteamProfitEntity checkBuyBuffItem(ItemGoods itemGoods){
        SellSteamProfitEntity entity = new SellSteamProfitEntity();
        entity.setItem_id(itemGoods.getId());
        entity.setName(itemGoods.getName());
        entity.setBuff_price(Double.valueOf(itemGoods.getSell_min_price()));
        entity.setSell_steam_price(itemGoods.getGoods_info().getSteam_price_cny());
        entity.setSell_num(itemGoods.getSell_num());
        //税后价格
        double in_fact_price = Double.parseDouble(entity.getSell_steam_price()) *
                0.85;
        entity.setIn_fact_sell_steam_price(Double.valueOf(String.format("%.3f", in_fact_price)));
        //buff购买价格
        double buff_price =entity.getBuff_price() * 1.025;
        entity.setInterest_rate(String.format("%.3f", buff_price / in_fact_price));
        entity.setUp_date(new Date());
        if (0.80 > buff_price / in_fact_price) {
            return entity;
        }
        return null;
    }

    /**
     * 校验购买
     * @param entity
     * @return
     */
    public Boolean checkBuyBuffItem( SellSteamProfitEntity entity){
        //税后价格
        double in_fact_price = Double.parseDouble(entity.getSell_steam_price()) *
                0.85;
        //buff购买价格
        double buff_price =entity.getBuff_price() * 1.025;
        entity.setInterest_rate(String.format("%.3f", buff_price / in_fact_price));
        entity.setUp_date(new Date());
        if (0.80 > buff_price / in_fact_price) {
           return true;
        }
        return false;
    }
}
