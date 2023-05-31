package com.chenerzhu.crawler.proxy.steam.service;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
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
     * 下架几页商品（一页等于十个）
     *
     * @param sum
     */
    public void unlistings(int sum) {
        for (int i = 0; i < sum; i++) {
            unlisting();
        }
    }

    /**
     * 取消被block的货物
     */
    public void unlisting() {
        String url = "https://steamcommunity.com/market";

        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        Document parse = Jsoup.parse(resStr);

        Element marketListingsRows = parse.getElementById("tabContentsMyActiveMarketListingsRows");
        //下架，已经上架的商品
        parseActiveMarketList(marketListingsRows);
        Elements market_content_block = parse.getElementsByClass("my_listing_section market_content_block market_home_listing_table");
        //取消需要审核的商品
        parseMarkBlockList(market_content_block);
    }

    /**
     * 解析已经上架的商品id集合
     */
    public void parseActiveMarketList(Element marketListingsRows) {
        if (marketListingsRows == null){
            log.info("获取上架信息失败");
        }
        try {
            int length = Math.min(10,marketListingsRows.children().size());
            for (int i = 0; i < length; i++) {
                SleepUtil.sleep(300);

                Element child = marketListingsRows.children().get(i);
                String id = child.id();
                if (StrUtil.isEmpty(id)) {
                    continue;
                }
                removeList(id.split("_")[1]);
            }
            log.info("最早上架的十个商品，已经取消");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 解析被阻塞的商品
     */
    public void parseMarkBlockList(Elements market_content_block) {
        if (market_content_block.size() == 0){
            return;
        }
        Element element = market_content_block.get(0);
        for (Element child : element.children()) {
            String id = child.id();
            if (StrUtil.isEmpty(id)) {
                continue;
            }
            removeList(id.split("_")[1]);
            SleepUtil.sleep(350);
        }
        log.info("被锁的商品，已经全部取消");
    }

    public void getMylistings() {
        String url = "https://steamcommunity.com/market/mylistings/render/?query=&start=1&count=100";
    }

    public void removeList(String id) {
        String url = "https://steamcommunity.com/market/removelisting/" + id;
        Map<String, String> paramerMap = new HashMap<>();
        Map<String, String> saleHeader = SteamConfig.getSaleHeader();
        paramerMap.put("sessionid", SteamConfig.getCookieOnlyKey("sessionid"));
        String responseStr = HttpClientUtils.sendPostForm(url, "", saleHeader, paramerMap);
        SleepUtil.sleep(500);
        log.info("需要审批的饰品下架信息：{}",responseStr);
    }
}
