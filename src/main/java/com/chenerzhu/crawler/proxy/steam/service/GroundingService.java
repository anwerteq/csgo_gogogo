package com.chenerzhu.crawler.proxy.steam.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellBuffProfitEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.SellBuffProfitRepository;
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

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Autowired
    SellBuffProfitRepository buffProfitRepository;
    /**
     * steam上架操作逻辑
     */
    public void productListingOperation() {
        executorService.execute(()->{
            //获取库存
            InventoryRootBean inventoryRootBean = getSteamInventory();
            Set<String> collect = buffProfitRepository.selectSellBuffItem().stream().map(SellBuffProfitEntity::getMarket_hash_name).collect(Collectors.toSet());
            //获取商品类的价格信息集合
            inventoryRootBean.getDescriptions().stream().forEach(description -> {
                //售卖到buff的商品，不上架
                if (collect.contains(description.getMarket_hash_name())){
                    return;
                }
                PriceVerviewRoot priceVerview = getPriceVerview(description.getMarket_hash_name());
                priceVerview.setClassid(description.getClassid());
                Assets assets = inventoryRootBean.getAssets().stream().filter(asset -> asset.getClassid().equals(priceVerview.getClassid())).findFirst().get();
                saleItem(assets.getAssetid(), priceVerview.getLowest_price(), assets.getAmount());
                log.info("steam商品上架完成:"+ priceVerview.getClassid());
            });
            log.info("steam全部商品上架完成");
        });

    }

    /**
     * 获取steam库存
     */
    private InventoryRootBean getSteamInventory() {
        SleepUtil.sleep();
        String url = "https://steamcommunity.com/inventory/76561199351185401/730/2?l=schinese&count=100&market=1";
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
     * @param price：到手价格
     */
    private void saleItem(String assetid, String price, String amount) {
        SleepUtil.sleep();
        Integer priceCount = null;
        if (price.startsWith("$")) {
            price = price.replace("$", "");
            priceCount = new BigDecimal(100 * Double.parseDouble(price) * 0.85).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        }
        Boolean flag = false;
        if (priceCount == null) {
            flag = true;
        }
        if (flag) {
            throw new ArithmeticException("参数异常");
        }
        Map<String, String> saleHeader = SteamConfig.getSaleHeader();
        String url = "https://steamcommunity.com/market/sellitem";
        Map<String, String> paramerMap = new HashMap<>();
        for (String cookie : saleHeader.get("Cookie").split(";")) {
            if ("sessionid".equals(cookie.split("=")[0].trim())) {
                paramerMap.put("sessionid", cookie.split("=")[1].trim());
                break;
            }
        }
        paramerMap.put("appid", "730");
        paramerMap.put("contextid", "2");
        paramerMap.put("assetid", assetid);
        paramerMap.put("amount", amount);
        paramerMap.put("price", String.valueOf(priceCount-1));
        String responseStr = HttpClientUtils.sendPostForm(url, "", saleHeader, paramerMap);
        System.out.println("1231231");
    }


}
