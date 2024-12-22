package com.xiaojuzi.steam.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.xiaojuzi.buff.service.itemordershistogram.ItemOrdershistogram;
import com.xiaojuzi.common.GameCommet;
import com.xiaojuzi.csgo.entity.ItemGoods;
import com.xiaojuzi.csgo.repository.IItemGoodsRepository;
import com.xiaojuzi.steam.SteamConfig;
import com.xiaojuzi.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * steam商品购买服务
 */

@Service
@Slf4j
public class SteamProfitService {

    @Autowired
    IItemGoodsRepository itemRepository;

    @Autowired
    SteamBuyItemService steamBuyItemService;

    @Value(("${sales_ratio_min}"))
    private Double salesRatio;

    @Value(("${sales_ratio_max}"))
    private Double salesRatioMax;


    /**
     * steam求购条件和逻辑
     *
     * @param itemGoods
     * @param sell_min_priceD
     * @param quantity
     */
    public void wantToBuy(ItemGoods itemGoods, Double sell_min_priceD, int quantity) {
        String marketName = itemGoods.getName();
        try {
            //steam的求购价
            Double price_total = Double.parseDouble(getItemordershistogram(itemGoods.getMarketHashName(), 8)) * 100;
            //steam的求购价 rmb
            Double price_totalRmb = price_total * 7.3;
            Double buySalesRatio = sell_min_priceD * 100 / price_totalRmb;
            if (buySalesRatio < salesRatio || buySalesRatio > salesRatioMax) {
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
        } catch (ArrayIndexOutOfBoundsException e) {
            log.info("获取饰品：{}求购数据失败,失败原因：此货物不在steam市场", hashName);
        } catch (Exception e) {
            log.info("获取饰品：{}求购数据异常信息：{}", hashName, e);
        }
        return itemNameId.trim();
    }
}
