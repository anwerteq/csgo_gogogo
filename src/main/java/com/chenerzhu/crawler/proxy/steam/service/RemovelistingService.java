package com.chenerzhu.crawler.proxy.steam.service;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * steam商品下架
 */
@Service
@Slf4j
public class RemovelistingService {

    /**
     * 取消被block的货物
     */
    public void unlisting(){
        String url = "https://steamcommunity.com/market";

        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        Document parse = Jsoup.parse(resStr);

        Element marketListingsRows = parse.getElementById("tabContentsMyActiveMarketListingsRows");
        //下架，已经上架的商品
        parseActiveMarketList(marketListingsRows);
        Elements market_content_block = parse.getElementsByClass("my_listing_section market_content_block market_home_listing_table");
        //取消需要审核的商品
        parseMarkBlockList(market_content_block);
        System.out.println("'");
    }

    /**
     * 解析已经上架的商品id集合
     */
    public void parseActiveMarketList(Element marketListingsRows){

        for (Element child : marketListingsRows.children()) {
            String id = child.id();
            if (StrUtil.isEmpty(id)){
                continue;
            }
            removeList(id.split("_")[1]);
        }
        log.info("最早上架的十个商品，已经取消");
    }

    /**
     * 解析被阻塞的商品
     */
    public  void parseMarkBlockList(Elements market_content_block){
        Element element = market_content_block.get(0);
        for (Element child : element.children()) {
            String id = child.id();
            if (StrUtil.isEmpty(id)){
                continue;
            }
            removeList(id.split("_")[1]);
        }
        log.info("被锁的商品，已经全部取消");
    }

    public void getMylistings(){
        String url = "https://steamcommunity.com/market/mylistings/render/?query=&start=1&count=100";
    }
    public void removeList(String id){
        String url = "https://steamcommunity.com/market/removelisting/" + id;
        Map<String, String> paramerMap = new HashMap<>();
        Map<String, String> saleHeader = SteamConfig.getSaleHeader();
        for (String cookie : saleHeader.get("Cookie").split(";")) {
            if ("sessionid".equals(cookie.split("=")[0].trim())) {
                paramerMap.put("sessionid", cookie.split("=")[1].trim());
                break;
            }
        }
        String responseStr = HttpClientUtils.sendPostForm(url, "", saleHeader, paramerMap);
    }
}
