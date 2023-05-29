package com.chenerzhu.crawler.proxy.steam.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellBuffProfitEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.SellBuffProfitRepository;
import com.chenerzhu.crawler.proxy.pool.csgo.service.BuffCostService;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.Assets;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.InventoryRootBean;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.PriceVerviewRoot;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

    /**
     * steam上架操作逻辑
     */
    public void productListingOperation() {
        //获取库存
        InventoryRootBean inventoryRootBean = getSteamInventory();
        if (inventoryRootBean.getDescriptions().isEmpty()) {
            log.info("未有需要上架的商品");
            return;
        }
        Set<String> collect = buffProfitRepository.selectSellBuffItem().stream().map(SellBuffProfitEntity::getMarket_hash_name).collect(Collectors.toSet());
        if (collect.isEmpty()) {
            log.info("buff推荐售卖的商品数据为空，禁止上架steam");
            return;
        }
        //获取商品类的价格信息集合
        inventoryRootBean.getDescriptions().stream().forEach(description -> {
            //售卖到buff的商品，不上架 old
            if (collect.contains(description.getMarket_hash_name())) {
                return;
            }
            //获取steam推荐的 税前售卖金额（美金）如： $0.03 美金
            PriceVerviewRoot priceVerview = getPriceVerview(description.getMarket_hash_name());
            priceVerview.setClassid(description.getClassid());
            Assets assets = inventoryRootBean.getAssets().stream().filter(asset -> asset.getClassid().equals(priceVerview.getClassid())).findFirst().get();
            if (StrUtil.isEmpty(priceVerview.getLowest_price())) {
                return;
            }
//            SleepUtil.sleep(1000);
            //获取steam推荐的的税后金额（美分） getLowest_price:是steam推荐的税前美金
            int afterTaxCentMoney = getAfterTaxCentMoney(priceVerview.getLowest_price());
            //获取购买成本的最低销售金额（美分）
            int lowCostCent = buffCostService.getLowCostCent(assets.getAssetid(), assets.getClassid(),description.getMarket_hash_name());
            //获取最大的销售金额
            int steamAfterTaxPrice = Math.max(afterTaxCentMoney, lowCostCent);
            //steam推荐的金额和buff售卖最低金额 选高的
            saleItem(assets.getAssetid(), steamAfterTaxPrice, assets.getAmount());
            log.info("steam商品上架完成:" + priceVerview.getClassid());
//            SleepUtil.sleep(1000);
        });
        log.info("steam全部商品上架完成");
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
        Double beforeTax = (100 * Double.parseDouble(beforeTaxPriceDollar) -1 );
        //税后美分
        Double afterTax = beforeTax * 0.8697;
        int value = afterTax.intValue();
        if (afterTax > value){
            value = value +1 ;
        }
        return value;
    }

    /**
     * 获取steam库存
     */
    private InventoryRootBean getSteamInventory() {
        SleepUtil.sleep();
        String url = "https://steamcommunity.com/inventory/" + SteamConfig.getSteamId() + "/730/2?l=schinese&count=30&market=1";
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
        SleepUtil.sleep();
        String url = null;
        try {
            url = "https://steamcommunity.com/market/priceoverview/?country=US&currency=1&appid=730&market_hash_name=" + URLEncoder.encode(market_hash_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        if (StrUtil.isEmpty(resStr)) {
            log.error("获取参数的参考价格失败");
            throw new ArithmeticException("获取参数的参考价格失败");
        }
        PriceVerviewRoot priceVerviewRoot = JSONObject.parseObject(resStr, PriceVerviewRoot.class);
        return priceVerviewRoot;
    }


    /**
     * 设置商品上架价格
     *
     * @param assetid：商品类目的id
     * @param steamAfterTaxPrice：售卖的税后美分
     */
    private void saleItem(String assetid, int steamAfterTaxPrice, String amount) {
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
         if (StrUtil.isEmpty(responseStr)){
            log.info("商品assetid-{}-上架失败",assetid);
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        String success = jsonObject.getString("success");
        if ("false".equals(success)){

            log.info("商品assetid-{}-上架失败，失败信息{}",assetid,jsonObject.getString("message"));

            return;
        }
        log.info("商品assetid-{}-上架成功",assetid);
    }
}
