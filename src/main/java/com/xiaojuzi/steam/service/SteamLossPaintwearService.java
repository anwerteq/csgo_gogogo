package com.xiaojuzi.steam.service;

import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.xiaojuzi.common.GameCommet;
import com.xiaojuzi.csgo.entity.ItemGoods;
import com.xiaojuzi.csgofloat.CsgoFloatService;
import com.xiaojuzi.steam.SteamConfig;
import com.xiaojuzi.steam.service.marketlist.MarketListJsonRootBean;
import com.xiaojuzi.steam.service.marketlist.SteamLossItemDetail;
import com.xiaojuzi.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * steam扫低磨损代码
 */
@Service
@Slf4j
public class SteamLossPaintwearService {

    @Autowired
    CsgoFloatService csgoFloatService;


    /**
     * 获取steam市场数据
     */
    public void getMarketLists(ItemGoods itemGoods, Map<String, String> sellPrices) {

        List<SteamLossItemDetail> marketLists = getMarketLists(itemGoods, sellPrices, 1);
        for (SteamLossItemDetail marketList : marketLists) {
            Boolean aBoolean = checkWearPainPrice(sellPrices, marketList);
            if (aBoolean) {
                //进行购买操作
                log.info("购买饰品");
            }
        }
        log.info("123123");
    }


    /**
     * 校验金额和磨损度
     *
     * @param sellPrices
     * @param detail
     * @return
     */
    public Boolean checkWearPainPrice(Map<String, String> sellPrices, SteamLossItemDetail detail) {
        for (Map.Entry<String, String> entry : sellPrices.entrySet()) {
            String[] split = entry.getKey().split("-");
            Double minWear = Double.valueOf(split[0]);
            Double maxWear = Double.valueOf(split[1]);
            Double painwear = Double.valueOf(detail.getPainwear());
            Boolean maxWearFlag = maxWear > painwear;
            Boolean minWearFlag = painwear > minWear;
            if (maxWearFlag && minWearFlag) {
                //entry.getValue() 人民币
                String value = entry.getValue();
                Double costPrice = Double.valueOf(detail.getPriceDollar()) * 7.31 * 0.75;
                Boolean flag = Double.valueOf(value) > costPrice;
                return flag;
            }
        }
        return false;
    }

    /**
     * 获取steam市场数据
     */
    public List<SteamLossItemDetail> getMarketLists(ItemGoods itemGoods, Map<String, String> sellPrices, Integer pageIndex) {
//        String hashName = itemGoods.getName();
//        String hashName = "StatTrak™ PP-Bizon | Night Riot (Field-Tested)";
        String hashName = itemGoods.getMarketHashName();
        String hashNameUrl = URLUtil.encode(hashName, "UTF-8").replace("+", "%20");
        String url = "https://steamcommunity.com/market/listings/" + GameCommet.getGameId() + "/" + hashNameUrl
                + "/render/?query=&start=" + (pageIndex - 1) * 100 + "&count=" + pageIndex * 100 + "&country=US&language=schinese&currency=1";
        Map<String, String> saleHeader = SteamConfig.getSteamHeader();
        String responseStr = HttpClientUtils.sendGet(url, saleHeader);
        MarketListJsonRootBean marketListJsonRootBean = JSONObject.parseObject(responseStr, MarketListJsonRootBean.class);
        //获取饰品对应的steam价格
        List<SteamLossItemDetail> itemDetails = parseHtml(marketListJsonRootBean.getResults_html());
        JSONObject listinginfo = marketListJsonRootBean.getListinginfo();
        for (Map.Entry<String, Object> entry : listinginfo.entrySet()) {
            String listId = entry.getKey();
            JSONObject value = (JSONObject) entry.getValue();
            JSONObject asset = value.getJSONObject("asset");
            String assetId = asset.getString("id");
            String link = asset.getJSONArray("market_actions").getJSONObject(0).getString("link");
            for (SteamLossItemDetail itemDetail : itemDetails) {
                if (listId.equals(itemDetail.getListId())) {
                    link = link.replace("%listingid%", listId).replace("%assetid%", assetId);
                    itemDetail.setUrl(link);
                    itemDetail.setName(itemGoods.getName());
                    itemDetail.setHashName(itemDetail.getHashName());
                    continue;
                }
            }
        }
        List<SteamLossItemDetail> itemDetails1 = csgoFloatService.postLossBulk(itemDetails);
        return itemDetails1;
    }


    /**
     * 解析html数据
     *
     * @param html
     * @return
     */
    public List<SteamLossItemDetail> parseHtml(String html) {
        Map<String, String> map = new HashMap<>();
        Document document = Jsoup.parse(html);
        List<SteamLossItemDetail> itemDetails = new ArrayList<>();
        Elements market_recent_listing_rows = document.getElementsByClass("market_recent_listing_row");
        for (Element market_recent_listing_row : market_recent_listing_rows) {
            String id = market_recent_listing_row.id();
            Elements market_listing_price_with_fee = market_recent_listing_row.getElementsByClass("market_listing_price_with_fee");
            for (Element element : market_listing_price_with_fee) {
                SteamLossItemDetail steamLossItemDetail = new SteamLossItemDetail();
                String text = element.text();
                steamLossItemDetail.setListId(id.split("_")[1]);
                steamLossItemDetail.setPriceDollar(text.trim().replace("$", ""));
                itemDetails.add(steamLossItemDetail);
            }
        }
        return itemDetails;
    }


}
