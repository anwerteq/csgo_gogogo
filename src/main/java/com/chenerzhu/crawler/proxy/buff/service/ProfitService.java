package com.chenerzhu.crawler.proxy.buff.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.applicationRunners.BuffApplicationRunner;
import com.chenerzhu.crawler.proxy.applicationRunners.SteamApplicationRunner;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.buff.service.itemordershistogram.ItemOrdershistogram;
import com.chenerzhu.crawler.proxy.common.GameCommet;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.BuffBuyItems;
import com.chenerzhu.crawler.proxy.csgo.entity.ItemGoods;
import com.chenerzhu.crawler.proxy.csgo.profitentity.SellBuffProfitEntity;
import com.chenerzhu.crawler.proxy.csgo.profitentity.SellSteamProfitEntity;
import com.chenerzhu.crawler.proxy.csgo.repository.IItemGoodsRepository;
import com.chenerzhu.crawler.proxy.csgo.repository.SellBuffProfitRepository;
import com.chenerzhu.crawler.proxy.csgo.repository.SellSteamProfitRepository;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.service.SteamBuyItemService;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
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

    @Value(("${sales_ratio}"))
    private Double salesRatio;

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

    /**
     * 保存推荐在steam购买的记录
     * falg:true 购买，false:不购买
     */
    public void saveSellBuffProfitEntity(ItemGoods itemGoods, Boolean isBuy) {
        String marketName = itemGoods.getName();
        int priceWhere = GameCommet.getPriceWhere();
        if (Double.parseDouble(itemGoods.getSell_min_price()) > priceWhere) {
            log.info("商品：{}，价格为：{}元，不符合小于：{}元求购要求", marketName, itemGoods.getSell_min_price(), priceWhere);
            return;
        }
        int quantity = getQuantity(itemGoods);
        if (quantity == 0) {
            return;
        }
        //获取最近几天的中位数
        Double dayMedianPrice = pullHistoryService.get20dayMedianPrice(itemGoods.getId(), 15);
        //获取buff
        String sell_min_price = itemGoods.getSell_min_price();
        Double sell_min_priceD = Double.valueOf(sell_min_price);
        if (sell_min_priceD > dayMedianPrice) {
            log.info("商品：{}，价格为：{}元，15天中位数为：{}元,不符合求购要求", marketName, sell_min_price, dayMedianPrice);
            return;
        }
        BuffUserData buffUserData = BuffApplicationRunner.buffUserDataThreadLocal.get();
        SteamApplicationRunner.setThreadLocalSteamId(buffUserData.getSteamId());
        try {
            //steam的求购价
            Double price_total = Double.parseDouble(getItemordershistogram(itemGoods.getMarketHashName(), 8)) * 100;
            //steam的求购价 rmb
            Double price_totalRmb = price_total * 7.3;
            Double buySalesRatio = sell_min_priceD * 100 / price_totalRmb;
            if (buySalesRatio < salesRatio) {
                log.info("商品：{}，比例为：{}，不符合求购要求:{}", itemGoods.getName(), buySalesRatio, salesRatio);
                return;
            }
            //求购价，去下订单
            log.info("商品：{}，符合要求，求购价为：{}美分，求购数量为：{}，开始去求购", marketName, price_total.intValue(), quantity);
            steamBuyItemService.createbuyorder(price_total.intValue(), itemGoods.getMarketHashName(), quantity, itemGoods.getName());
            log.info("商品：{}，求购结束", marketName);
        } catch (Exception e) {
            log.error("steam下订单异常信息：", e);
        }
        SleepUtil.sleep(5000);
        if (true) {
            return;
        }
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
        //几折
        double interest_rate = (profit.getSell_min_price() / (profit.getIn_fact_steam_price_cny()));
        profit.setInterest_rate(String.format("%.3f", interest_rate));
        profit.setUp_date(new Date());
        profit.setMarket_hash_name(itemGoods.getMarketHashName());
        Boolean flag = false;
        if (0.98 < interest_rate) {
            //在buff售卖，利率超过3%
            flag = true;
        }
        if (flag) {
            sellBuffProfitRepository.save(profit);
        }
//        if (!isBuy) {
//            return;
//        }
        if (flag && Integer.parseInt(profit.getSell_num()) > 100 && profit.getSell_min_price() < 50) {
            //去steam下订单
            if (StrUtil.isEmpty(profit.getMarket_hash_name())) {
                return;
            }
        }

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

    /**
     * 获取第count个求购价格
     *
     * @return
     */
    private String getItemordershistogram(String hashName, int count) {
        String itemNameId = getItemNameId(hashName);
        String url = "https://steamcommunity.com/market/itemordershistogram?country=US&language=schinese&currency=1&two_factor=0&item_nameid=" + itemNameId;
        String responseStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        ItemOrdershistogram ordershistogram = JSONObject.parseObject(responseStr, ItemOrdershistogram.class);
        List<List<String>> buyOrderGraph = ordershistogram.getBuyOrderGraph();
        int saleCount = 0;
        for (int i = 0; i < buyOrderGraph.size(); i++) {
            List<String> list = buyOrderGraph.get(i);
            //求购数量
            saleCount = saleCount + Integer.parseInt(list.get(1));
            //求购价格
            String salePrice = list.get(0);
            //第一行求购数量大于count 加价求购
            if (i == 0 && saleCount > count) {
                Double salePricef = Double.valueOf(salePrice) + 0.01;
                return String.valueOf(salePricef);
            }
            if (saleCount > count) {
                return list.get(0);
            }
        }
        return "0";
    }


    /**
     * 获取steam订单信息
     *
     * @param hashName
     */
    public String getListsDetail(String hashName) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String hashNameUrl = URLUtil.encode(hashName, "UTF-8").replace("+", "%20");
        String url = "https://steamcommunity.com/market/listings/" + GameCommet.getGameId() + "/" + hashNameUrl;
        Map<String, String> saleHeader = SteamConfig.getSteamHeader();
        String responseStr = HttpClientUtils.sendGet(url, saleHeader);
        String itemNameId = "";
        try {
            itemNameId = responseStr.split("Market_LoadOrderSpread\\(")[1].split("\\)")[0];
        } catch (Exception e) {
            log.info("获取饰品：{}求购数据异常信息：{}", hashName, e);
        }
        return itemNameId.trim();
    }

    /**
     * 根据hashName获取steam对应的itemNameId
     *
     * @param hashName
     * @return
     */
    public String getItemNameId(String hashName) {
        ItemGoods itemGoods = itemRepository.findByMarketHashName(hashName);
        if (ObjectUtil.isNotNull(itemGoods) && StrUtil.isNotEmpty(itemGoods.getNameId())) {
            return itemGoods.getNameId().trim();
        }
        String nameId = getListsDetail(hashName);
        return nameId.trim();
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
        entity.setItem_id(itemGoods.getId());
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
