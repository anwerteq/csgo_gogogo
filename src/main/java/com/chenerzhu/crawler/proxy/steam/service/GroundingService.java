package com.chenerzhu.crawler.proxy.steam.service;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.csgo.entity.BuffCostEntity;
import com.chenerzhu.crawler.proxy.csgo.repository.SellBuffProfitRepository;
import com.chenerzhu.crawler.proxy.csgo.service.BuffCostService;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Assets;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.InventoryRootBean;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Owner_descriptions;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.PriceVerviewRoot;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.entity.Descriptions;
import com.chenerzhu.crawler.proxy.steam.repository.DescriptionsRepository;
import com.chenerzhu.crawler.proxy.steam.repository.SteamCostRepository;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.util.SteamTheadeUtil;
import com.chenerzhu.crawler.proxy.util.steamlogin.SteamUserDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * steam上架商品的服务
 */
@Service
@Slf4j
public class GroundingService {


    @Autowired
    SellBuffProfitRepository buffProfitRepository;

    @Autowired
    BuffCostService buffCostService;

    @Autowired
    SteamCostRepository steamCostRepository;

    @Autowired
    SteamBuyItemService steamBuyItemService;

    @Autowired
    DescriptionsRepository descriptionsRepository;

    @Autowired
    SteamInventoryService steamInventoryService;

    /**
     * steam上架操作逻辑
     */
    public void productListingOperation() {
        //刷新库存信息
        steamInventoryService.refreshSteamInventory();
        ThreadUtil.sleep(5 * 1000);
        SteamUserDate steamUserDate = SteamTheadeUtil.steamUserDateTL.get();
        List<Descriptions> allBySteamId = descriptionsRepository.findAllBySteamId(steamUserDate.getSession().getSteamID());
        for (Descriptions descriptions : allBySteamId) {
            Double buyPrice = descriptions.getBuy_price();
            Double buffMinPrice = descriptions.getBuff_min_price();
            String marketHashName = descriptions.getMarket_hash_name();
            //成本价远远小于，此时售卖价，上架到steam市场
            //获取steam推荐的 税前售卖金额（美金）如： $0.03 美金
            PriceVerviewRoot priceVerview = getPriceVerview(marketHashName);
            String lowestPrice = priceVerview.getLowest_price();
            if (StrUtil.isEmpty(lowestPrice)) {
                lowestPrice = priceVerview.getMedian_price();
            }
            // steam最低价
            String lowest_price = lowestPrice.replace("$", "");
            Double steamPrice = Double.valueOf(lowest_price) * 1.4 * 100;
            if (buyPrice == null) {
                //没有购买成本金额，不适合倒卖的，上架市场
                buyPrice = Double.valueOf(lowest_price);
            }
            //成本价大于售卖价
            if (buyPrice * 6 > buffMinPrice) {
                try {
                    //购买价格
                    Double buffPrice = buyPrice  * 115;
                    double max = Math.max(buffPrice, steamPrice);
                    //steam推荐的金额和buff售卖最低金额 选高的
                    saleItem(descriptions, (int) max, descriptions.getAmount() + "");
                } catch (Exception e) {
                    log.error("上架商品失败，失败信息：{}", e);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
        log.info("steam全部商品上架完成");
    }

    /**
     * 获取过期时间
     *
     * @return
     */
    public Date getExpirationTime(List<Owner_descriptions> owner_descriptions) {
        Owner_descriptions owner = owner_descriptions.get(1);
        String[] split = owner.getValue().split("\\)");
        String ownerStr = split[0];
        String year = ownerStr.split(" ")[0];
        String month = ownerStr.split(" ")[1].split("月")[0];
//        month = String.format("%02d", month);
        String day = ownerStr.split(" ")[2];
        //“格林尼治早上7点是北京时间下午3点
        Date date = new Date(Integer.parseInt(year) - 1900, Integer.parseInt(month), Integer.parseInt(day), 13, 0, 0);
        return date;
    }


    /**
     * 获取steam的销售价格
     *
     * @param priceVerview
     * @param assets
     * @param description
     * @return
     */
    public int getSteamAfterTaxPrice(PriceVerviewRoot priceVerview, Assets assets, Descriptions description) {
        //获取steam推荐的的税后金额（美分） getLowest_price:是steam推荐的税前美金
        int afterTaxCentMoney = getAfterTaxCentMoney(priceVerview.getLowest_price());
        //获取购买成本的最低销售金额（美分）
        BuffCostEntity buffCostEntity = buffCostService.getLowCostCent(assets.getAssetid(), assets.getClassid()
                , description.getMarket_hash_name(), afterTaxCentMoney);
        //没有记录，直接使用steam推荐价格
        if (buffCostEntity == null) {
            return 0;
        }
        return buffCostEntity.getReturned_money() / 7;
    }

    /**
     * 计算出steam的推荐税后美分
     *
     * @param beforeTaxPriceDollar：steam当前售卖的最低美金
     * @return
     */
    public int getAfterTaxCentMoney(String beforeTaxPriceDollar) {
        beforeTaxPriceDollar = beforeTaxPriceDollar.replace("$", "");
        //税前美分
        Double beforeTax = (100 * Double.parseDouble(beforeTaxPriceDollar) - 1);
        //税后美分
        Double afterTax = beforeTax * 0.8697;
        int value = afterTax.intValue();
        if (afterTax > value) {
            value = value + 1;
        }
        return value;
    }


    /**
     * 获取steam库存
     */
    private InventoryRootBean getSteamInventory() {
        SleepUtil.sleep();
        String url = "https://steamcommunity.com/inventory/" + SteamConfig.getSteamId() + "/730/2?l=schinese&count=2000&market=1";
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        if (StrUtil.isEmpty(resStr)) {
            log.error("获取steam库存失败");
            throw new ArithmeticException("获取steam库存失败");
        }
        InventoryRootBean inventoryRootBean = JSONObject.parseObject(resStr, InventoryRootBean.class);
        return inventoryRootBean;
    }


    /**
     * 获取商品的销售参考价格
     *
     * @param market_hash_name：steam商品的唯一值
     * @return
     */
    private PriceVerviewRoot getPriceVerview(String market_hash_name) {
        String url = null;
        try {
            Thread.sleep(4 * 1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            url = "https://steamcommunity.com/market/priceoverview/?country=US&currency=1&appid=730&market_hash_name=" + URLEncoder.encode(market_hash_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        if (StrUtil.isEmpty(resStr)) {
            log.error("获取参数的参考价格失败");
            return null;
        }
        PriceVerviewRoot priceVerviewRoot = JSONObject.parseObject(resStr, PriceVerviewRoot.class);
        return priceVerviewRoot;
    }


    /**
     * 设置商品上架价格
     *
     * @param descriptions：商品类目的id
     * @param steamAfterTaxPrice：售卖的税后美分
     */
    private void saleItem(Descriptions descriptions, int steamAfterTaxPrice, String amount) {
        ThreadUtil.sleep(5 * 1000);
        String name = descriptions.getName();
        String assetid = descriptions.getAssetid();
        Map<String, String> saleHeader = SteamConfig.getSaleHeader();
        String url = "https://steamcommunity.com/market/sellitem";
        Map<String, String> paramerMap = new HashMap<>();
        paramerMap.put("sessionid", SteamConfig.getCookieOnlyKey("sessionid"));
        paramerMap.put("appid", "730");
        paramerMap.put("contextid", "2");
        paramerMap.put("assetid", assetid);
        paramerMap.put("amount", amount);
        paramerMap.put("price", String.valueOf(steamAfterTaxPrice));
        String responseStr = HttpClientUtils.sendPostForm(url, "", saleHeader, paramerMap);
        if (StrUtil.isEmpty(responseStr)) {
            log.info("steam商品名称-{}-上架失败", name);
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        String success = jsonObject.getString("success");
        if ("false".equals(success)) {
            log.info("steam商品名称-{}-上架失败，失败信息{}", name, jsonObject.getString("message"));
            return;
        }
        log.info("steam商品名称-{}-上架成功，上架成功是否需要确认（1：是，0：否）:{}", name, jsonObject.getString("requires_confirmation"));
    }
}
