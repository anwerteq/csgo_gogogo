package com.chenerzhu.crawler.proxy.steam.service;

import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.common.GameCommet;
import com.chenerzhu.crawler.proxy.csgo.entity.ItemGoods;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.service.csgoFloat.FloatBulk;
import com.chenerzhu.crawler.proxy.steam.service.marketlist.MarketListJsonRootBean;
import com.chenerzhu.crawler.proxy.steam.service.marketlist.SteamLossItemDetail;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * steam扫低磨损代码
 */
@Service
@Slf4j
public class SteamLossPaintwearService {


    /**
     * 获取steam市场数据
     */
    public void getMarketLists(ItemGoods itemGoods, Map<String, String> sellPrices) {

        List<SteamLossItemDetail> marketLists = getMarketLists(itemGoods, sellPrices, 1);

        log.info("123123");
    }


    /**
     * 获取steam市场数据
     */
    public List<SteamLossItemDetail> getMarketLists(ItemGoods itemGoods, Map<String, String> sellPrices, Integer pageIndex) {
//        String hashName = itemGoods.getName();
        String hashName = "StatTrak™ PP-Bizon | Night Riot (Field-Tested)";
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
                    continue;
                }
            }
        }
        List<SteamLossItemDetail> itemDetails1 = postBulk(itemDetails);
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


    /**
     * 获取磨损数据数据
     *
     * @param details
     * @return
     */
    public List<SteamLossItemDetail> postBulk(List<SteamLossItemDetail> details) {
        String url = "http://localhost:8086/bulk";
        List<Map<String, String>> links = new ArrayList<>();
        for (SteamLossItemDetail detail : details) {
            Map<String, String> hashMap = new HashMap();
            hashMap.put("link", detail.getUrl());
            links.add(hashMap);
        }
        HashMap hashMap = new HashMap();
        hashMap.put("links", links);
        String reponse = HttpClientUtils.sendPost(url, JSONObject.toJSONString(hashMap), new HashMap<>());
        JSONObject jsonObject = JSONObject.parseObject(reponse);
        //磨损信息
        List<FloatBulk> floatBulks = jsonObject.entrySet().stream().map(entrySet -> {
            FloatBulk floatBulk = new FloatBulk();
            BeanUtils.copyProperties(entrySet.getValue(), floatBulk);
            floatBulk.setLinkKey(entrySet.getKey());
            return floatBulk;
        }).collect(Collectors.toList());
        Map<String, Double> linkAndFloatValueMap = floatBulks.stream().collect(Collectors.toMap(FloatBulk::getLinkKey, FloatBulk::getFloatvalue));
        for (SteamLossItemDetail detail : details) {
            String linkKey = detail.getLinkKey();
            detail.setPainwear(String.valueOf(linkAndFloatValueMap.get(linkKey)));
        }
        return details;
    }
}
