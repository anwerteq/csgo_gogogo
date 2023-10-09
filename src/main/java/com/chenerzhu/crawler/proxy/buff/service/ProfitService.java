package com.chenerzhu.crawler.proxy.buff.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.applicationRunners.BuffApplicationRunner;
import com.chenerzhu.crawler.proxy.applicationRunners.SteamApplicationRunner;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.buff.service.itemordershistogram.ItemOrdershistogram;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.BuffBuyItems;
import com.chenerzhu.crawler.proxy.csgo.entity.ItemGoods;
import com.chenerzhu.crawler.proxy.csgo.profitentity.SellBuffProfitEntity;
import com.chenerzhu.crawler.proxy.csgo.profitentity.SellSteamProfitEntity;
import com.chenerzhu.crawler.proxy.csgo.repository.IItemGoodsRepository;
import com.chenerzhu.crawler.proxy.csgo.repository.SellBuffProfitRepository;
import com.chenerzhu.crawler.proxy.csgo.repository.SellSteamProfitRepository;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.service.SteamBuyItemService;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * buff利率服务
 */
@Service
@Slf4j
public class ProfitService {
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
        if (Double.parseDouble(itemGoods.getSell_min_price()) > 200) {
            return;
        }
        if (Double.parseDouble(itemGoods.getSell_min_price()) < 2) {
            return;
        }
        int quantity = getQuantity(itemGoods);
        if (quantity == 0) {
            return;
        }
        //获取最近几天的中位数
        Double dayMedianPrice = pullHistoryService.get20dayMedianPrice(itemGoods.getId(), 30);
        //获取buff
        String sell_min_price = itemGoods.getSell_min_price();
        if (Double.valueOf(sell_min_price) > dayMedianPrice) {
            return;
        }
        BuffUserData buffUserData = BuffApplicationRunner.buffUserDataThreadLocal.get();
        SteamApplicationRunner.setThreadLocalSteamId(buffUserData.getSteamId());
        //steam的求购价
        Double price_total = Double.parseDouble(getItemordershistogram(itemGoods.getMarketHashName(), 10)) * 100;
        try {
            //求购价，去下订单
            steamBuyItemService.createbuyorder(price_total, itemGoods.getMarketHashName(), quantity);

        } catch (Exception e) {
            log.error("steam下订单信息：", e);
        }

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
        int quantity = 0;
        Double min_price = Double.valueOf(itemGoods.getSell_min_price());
        Double steam_price_cny = Double.valueOf(itemGoods.getGoods_info().getSteam_price_cny());
//        if (min_price < steam_price_cny * 0.85) {
        if (min_price < steam_price_cny * 0.5) {
            return 0;
        }
        int sell_num = itemGoods.getBuy_num();
        if (sell_num < 10) {
            return 0;
        } else if (sell_num < 50) {
            quantity = 3;
        } else if (sell_num < 100) {
            quantity = 5;
        } else if (sell_num < 120) {
            quantity = 6;
        } else if (sell_num < 180) {
            quantity = 9;
        } else if (sell_num < 280) {
            quantity = 10;
        } else if (sell_num < 500) {
            quantity = 15;
        } else if (sell_num < 1000) {
            quantity = 22;
        } else {
            quantity = 30;
        }
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
        for (List<String> list : buyOrderGraph) {
            count = count - Integer.parseInt(list.get(1));
            if (count < 0) {
                return list.get(0);
            }
        }
        return "";
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
        String url = "https://steamcommunity.com/market/listings/730/" + hashNameUrl;
        Map<String, String> saleHeader = SteamConfig.getSteamHeader();
        String responseStr = HttpClientUtils.sendGet(url, saleHeader);
        String itemNameId = responseStr.split("Market_LoadOrderSpread\\( ")[1].split("\\)")[0];
        System.out.println("123123");
        return itemNameId;
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
}
