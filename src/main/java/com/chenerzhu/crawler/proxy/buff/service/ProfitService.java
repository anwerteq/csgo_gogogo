package com.chenerzhu.crawler.proxy.buff.service;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity.BuffBuyItems;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.ItemGoods;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellBuffProfitEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellSteamProfitEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.SellBuffProfitRepository;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.SellSteamProfitRepository;
import com.chenerzhu.crawler.proxy.steam.service.SteamBuyItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * buff利率服务
 */
@Service
public class ProfitService {
    @Autowired

    SellBuffProfitRepository sellBuffProfitRepository;


    @Autowired
    SellSteamProfitRepository sellSteamProfitRepository;

    @Autowired
    SteamBuyItemService steamBuyItemService;

    /**
     * 保存推荐在steam购买的记录
     */
    public void saveSellBuffProfitEntity(ItemGoods itemGoods) {
        SellBuffProfitEntity profit = new SellBuffProfitEntity();
        profit.setItem_id(itemGoods.getId());
        profit.setName(itemGoods.getName());
        profit.setSteam_price_cny(Double.parseDouble(itemGoods.getGoods_info().getSteam_price_cny()));
        profit.setIn_fact_steam_price_cny(Double.valueOf(String.format("%.3f", profit.getSteam_price_cny())));
        profit.setSell_min_price(Double.valueOf(itemGoods.getSell_min_price()));
        profit.setQuick_price(Double.valueOf(itemGoods.getQuick_price()));
        profit.setSell_num(String.valueOf(itemGoods.getSell_num()));
        //buff售卖=steam购买
        double interest = profit.getSell_min_price() - profit.getIn_fact_steam_price_cny();
        //赚的几率
        double interest_rate = (interest / profit.getIn_fact_steam_price_cny() * 100);
        profit.setInterest_rate(String.format("%.3f", interest_rate));
        profit.setUp_date(new Date());
        profit.setMarket_hash_name(itemGoods.getMarket_hash_name());
        Boolean flag = false;
        if (3.0f < interest_rate) {
            //在buff售卖，利率超过3%
            flag = true;
        }
        if (flag) {
            sellBuffProfitRepository.save(profit);
        }

        if (interest_rate > 3 && Integer.parseInt(profit.getSell_num()) > 30 && profit.getSell_min_price() < 50) {
            //去steam下订单
            if (StrUtil.isEmpty(profit.getMarket_hash_name())) {
                return;
            }
            steamBuyItemService.createbuyorder(Double.parseDouble(itemGoods.getGoods_info().getSteam_price()) * 100, profit.getMarket_hash_name());
        }

    }


    /**
     * 保存在steam售卖的购买记录
     */
    public void saveSellSteamProfit(ItemGoods itemGoods) {
        SellSteamProfitEntity item = checkBuyBuffItem(itemGoods);
        if (item != null) {
            sellSteamProfitRepository.save(item);
        }
    }


    /**
     * @param itemGoods
     * @return 返回null 不推荐购买
     */
    public SellSteamProfitEntity checkBuyBuffItem(ItemGoods itemGoods) {
        SellSteamProfitEntity entity = new SellSteamProfitEntity();
        entity.setItem_id(itemGoods.getId());
        entity.setName(itemGoods.getName());
        entity.setBuff_price(Double.valueOf(itemGoods.getSell_min_price()));
        entity.setSell_steam_price(itemGoods.getGoods_info().getSteam_price_cny());
        entity.setSell_num(itemGoods.getSell_num());
        entity.setHash_name(itemGoods.getMarket_hash_name());
        //税后价格
        double in_fact_price = Double.parseDouble(entity.getSell_steam_price()) *
                0.85;
        entity.setIn_fact_sell_steam_price(Double.valueOf(String.format("%.3f", in_fact_price)));
        //buff购买价格
        double buff_price = entity.getBuff_price() * 1.025;
        entity.setInterest_rate(String.format("%.3f", buff_price / in_fact_price));
        entity.setUp_date(new Date());
        if (0.80 > buff_price / in_fact_price) {
            return entity;
        }
        return null;
    }


    /**
     * 校验buff商品是否购买
     *
     * @param buffBuyItems：需要购买的
     * @param steamSellPriceDollar:单元美分
     * @return
     */
    public Boolean checkBuyItemOrder(BuffBuyItems buffBuyItems, int steamSellPriceDollar) {
        //到手需要打的折
        double takeTax = Double.valueOf(0.87f);
        //汇率
        int exchangeRate = 7;
        //计算税后人民币的价格 f分
        Double afterRateRMB = steamSellPriceDollar * takeTax * exchangeRate;
        Double costMoney = Double.parseDouble(buffBuyItems.getPrice()) * 100;
        //没啥钱，先买便宜的   成本在5毛 以下 和2快以上的都不买
        boolean isflag = costMoney < 50 || costMoney > 200;
        if (isflag) {
            return false;
        }
        //成本是税后的7.5折，可以购买
        if (costMoney / afterRateRMB <= 7.5) {
            return true;
        }
        return false;
    }


    /**
     * 校验购买
     *
     * @param entity
     * @return
     */
    public Boolean checkBuyBuffItem(SellSteamProfitEntity entity) {
        //税后价格
        double in_fact_price = Double.parseDouble(entity.getSell_steam_price()) *
                0.85;
        //buff购买价格
        double buff_price = entity.getBuff_price() * 1.025;
        entity.setInterest_rate(String.format("%.3f", buff_price / in_fact_price));
        entity.setUp_date(new Date());
        if (0.80 > buff_price / in_fact_price) {
            return true;
        }
        return false;
    }

    public Map<String, Long> selectItemIdANdHashName() {
        List<Map<String, String>> maps = sellSteamProfitRepository.selectItemIdANdHashName();
        List<SellSteamProfitEntity> list = new ArrayList<>();
        Map<String, Long> hashNameAndItemId = new HashMap<>();
        for (Map<String, String> map : maps) {
            hashNameAndItemId.put(map.get("hash_name"), Long.valueOf(map.get("item_id")));
        }
        return hashNameAndItemId;
    }
}
