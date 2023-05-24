package com.chenerzhu.crawler.proxy.pool.steam.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.InventoryRootBean;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity.PriceVerviewRoot;
import com.chenerzhu.crawler.proxy.pool.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.pool.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * steam上架商品的服务
 */
@Service
@Slf4j
public class GroundingService {

    /**
     * steam上架操作逻辑
     */
    public void productListingOperation() {
        //获取库存
        InventoryRootBean inventoryRootBean = getSteamInventory();
        //获取商品类的价格信息集合
        List<PriceVerviewRoot> priceVerviewRoots = inventoryRootBean.getDescriptions().stream().map(description -> {
            PriceVerviewRoot priceVerview = getPriceVerview(description.getMarket_hash_name());
            priceVerview.setClassid(description.getClassid());
            return priceVerview;
        }).collect(Collectors.toList());
        //根据库存 商品类信息 上架
        inventoryRootBean.getAssets().forEach(asset -> {
            //查找该类的推荐价格
            PriceVerviewRoot pricePo = priceVerviewRoots.stream().filter(pricePo1 -> asset.getClassid().equals(pricePo1.getClassid())).findFirst().get();
            if (pricePo == null) {
                return;
            }
            saleItem(asset.getAssetid(), pricePo.getLowest_price(), asset.getAmount());
        });
        log.info("steam全部商品上架完成");
    }

    /**
     * 获取steam库存
     */
    private InventoryRootBean getSteamInventory() {
        SleepUtil.sleep();
        String url = "https://steamcommunity.com/inventory/76561199351185401/730/2?l=schinese&count=75&market=1";
        String resStr = HttpClientUtils.sendGet(url, getSteamHeader());
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
        String resStr = HttpClientUtils.sendGet(url, getSteamHeader());
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
        Map<String, String> saleHeader = getSaleHeader();
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
        paramerMap.put("price", String.valueOf(priceCount));
        String responseStr = HttpClientUtils.sendPostForm(url, "", saleHeader, paramerMap);
        System.out.println("1231231");
    }

    /**
     * steam请求头
     *
     * @return
     */
    public static Map<String, String> getSteamHeader() {
        Map<String, String> headers1 = new HashMap() {{
            //steam请求头
            put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            put("Cookie", SteamConfig.STEAM_COOKIE);
            put("Host", "steamcommunity.com");
            put("Referer", "https://steamcommunity.com/profiles/76561199351185401/inventory?modal=1&market=1");
        }};
        return headers1;
    }


    /**
     * 获取上架需要的请求头
     */
    public static Map<String, String> getSaleHeader() {
        Map<String, String> steamHeader = getSteamHeader();
        steamHeader.put("Referer", "https://steamcommunity.com/profiles/76561199351185401/inventory?modal=1&market=1");
        steamHeader.put("Content-Type", "application/x-www-form-urlencoded");
        return steamHeader;
    }
}
