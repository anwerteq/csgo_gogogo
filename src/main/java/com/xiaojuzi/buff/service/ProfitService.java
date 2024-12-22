package com.xiaojuzi.buff.service;

import com.xiaojuzi.applicationRunners.BuffApplicationRunner;
import com.xiaojuzi.applicationRunners.SteamApplicationRunner;
import com.xiaojuzi.buff.BuffUserData;
import com.xiaojuzi.common.GameCommet;
import com.xiaojuzi.csgo.BuffBuyItemEntity.BuffBuyItems;
import com.xiaojuzi.csgo.entity.ItemGoods;
import com.xiaojuzi.csgo.profitentity.SellSteamProfitEntity;
import com.xiaojuzi.csgo.repository.IItemGoodsRepository;
import com.xiaojuzi.csgo.repository.SellBuffProfitRepository;
import com.xiaojuzi.csgo.repository.SellSteamProfitRepository;
import com.xiaojuzi.steam.service.SteamBuyItemService;
import com.xiaojuzi.steam.service.SteamProfitService;
import com.xiaojuzi.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * buff利率服务
 */
@Service
@Slf4j
public class ProfitService implements ApplicationRunner {


    @Value(("${sales_ratio_min}"))
    private Double salesRatio;

    @Value(("${want_to_buy}"))
    private String wantToBuy;


    @Value(("${mean_ratio_min}"))
    private Double mean_ratio_min;

    @Value(("${mean_ratio_max}"))
    private Double mean_ratio_max;

    @Autowired
    SellBuffProfitRepository sellBuffProfitRepository;

    @Autowired
    SellSteamProfitRepository sellSteamProfitRepository;

    @Autowired
    SteamBuyItemService steamBuyItemService;

    @Autowired
    IItemGoodsRepository itemRepository;

    @Autowired
    PullHistoryService pullHistoryService;

    @Autowired
    SteamProfitService steamProfitService;

    @Autowired
    ItemDetailService itemDetailService;

    /**
     * 保存推荐在steam购买的记录
     * falg:true 购买，false:不购买
     */
    public void saveSellBuffProfitEntity(ItemGoods itemGoods, Boolean isBuy) {
        String marketName = itemGoods.getName();
        int priceWhere = GameCommet.getPriceWhere();
        if (itemGoods.getSell_min_price() > priceWhere) {
            log.info("商品：{}，价格为：{}元，不符合小于：{}元求购要求", marketName, itemGoods.getSell_min_price(), priceWhere);
            return;
        }
        int quantity = getQuantity(itemGoods);
        if (quantity == 0) {
            return;
        }
        //校验buff的条件
        Double sell_min_priceD = checkBuffPriceDate(itemGoods);
        if (sell_min_priceD == 0){
            return;
        }
        BuffUserData buffUserData = BuffApplicationRunner.buffUserDataThreadLocal.get();
        SteamApplicationRunner.setThreadLocalSteamId(buffUserData.getSteamId());
        //求购商品
        if ("1".equals(wantToBuy)){
            steamProfitService.wantToBuy(itemGoods,sell_min_priceD,quantity);
        }else if ("2".equals(wantToBuy)){
            //扫低磨损
            itemDetailService.autoButSteam(itemGoods);
        }


        SleepUtil.sleep(5000);

    }

    /**
     * 校验buff的条件
     * @return
     */
    public Double checkBuffPriceDate(ItemGoods itemGoods) {
        //获取最近几天的中位数
        Double dayMedianPrice = pullHistoryService.get20dayMedianPrice(itemGoods.getMarketHashName(), 20);
        //获取buff
        Double sell_min_price = itemGoods.getSell_min_price();
        Double sell_min_priceD = Double.valueOf(sell_min_price);
        Double meanRatio = sell_min_priceD / dayMedianPrice;
        if (meanRatio < mean_ratio_min || meanRatio > mean_ratio_max) {
            log.info("商品：{} 不符合要求，价格为：{}元，20天中位数为：{}元,饰品均值比例:{},下限比例为:{},上限比例为:{}"
                    , itemGoods.getName(), sell_min_price, dayMedianPrice, meanRatio, mean_ratio_min, mean_ratio_max);
            return 0.0;
        }
        return sell_min_priceD;
    }


    /**
     * 获取需要求购的数量
     *
     * @return
     */
    public int getQuantity(ItemGoods itemGoods) {

        Double min_price = Double.valueOf(itemGoods.getSell_min_price());
        Double steam_price_cny = Double.valueOf(itemGoods.getGoods_info().getSteam_price_cny());
        Double buySalesRatio = min_price / steam_price_cny;
        if (buySalesRatio < salesRatio - 0.2) {
            log.info("商品：{}，比例为：{}，不符合[buff在售价/steam在售价]：{}求购要求", itemGoods.getName(), buySalesRatio, salesRatio - 0.2);
            return 0;
        }
        int sell_num = itemGoods.getSell_num();
        int quantity = GameCommet.getQuantity(sell_num);
        return quantity;
    }

    public static void main(String[] args) {
        try {
            System.out.println(URLEncoder.encode("M4A4 | Neo-Noir (Field-Tested)", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
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
        entity.setItem_id(itemGoods.getMarketHashName());
        entity.setName(itemGoods.getName());
        entity.setBuff_price(Double.valueOf(itemGoods.getSell_min_price()));
        entity.setSell_steam_price(itemGoods.getGoods_info().getSteam_price_cny());
        entity.setSell_num(itemGoods.getSell_num());
        entity.setHash_name(itemGoods.getMarketHashName());
        //税后价格
        double in_fact_price = Double.parseDouble(entity.getSell_steam_price()) *
                0.85;
        entity.setIn_fact_sell_steam_price(Double.valueOf(String.format("%.3f", in_fact_price)));
        //buff购买价格
        double buff_price = entity.getBuff_price() * 1.025;
        entity.setInterest_rate(String.format("%.3f", buff_price / in_fact_price));
        entity.setUp_date(new Date());
        if (0.85 > buff_price / in_fact_price) {
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
        //没啥钱，先买便宜的   成本在1元以下 和5快以上的都不买
        boolean isflag = costMoney < 80 || costMoney > 500;
        if (isflag) {
            return false;
        }
        //成本是税后的7.5折，可以购买
        return costMoney / afterRateRMB <= 7.3;
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
        double buff_price = entity.getBuff_price();
        entity.setInterest_rate(String.format("%.3f", buff_price / in_fact_price));
        entity.setUp_date(new Date());
        double in_fact = buff_price / in_fact_price;
        return 0.78 >= in_fact;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {
    }
}
