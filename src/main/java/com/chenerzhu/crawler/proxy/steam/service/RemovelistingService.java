package com.chenerzhu.crawler.proxy.steam.service;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.applicationRunners.SteamApplicationRunner;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.util.SteamTheadeUtil;
import com.chenerzhu.crawler.proxy.util.steamlogin.SteamUserDate;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.*;

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
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 取消被block的货物
     */
    public void unlisting() {

        //获取上架的饰品id
        Set<String> mylistings = getMylistings();
        for (String mylisting : mylistings) {
           try {
               removeList(mylisting);
           }catch (Exception e){
               log.info("下架失败",e);
           }
        }
    }

    /**
     * 解析已经上架的商品id集合
     */
    public void parseActiveMarketList(Element marketListingsRows) {
        if (marketListingsRows == null){
            log.info("获取上架信息失败");
        }
        try {
            int length = Math.max(10, marketListingsRows.children().size());
            for (int i = 0; i < length; i++) {
//                SleepUtil.sleep(300);

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

    /**
     * 获取上架的steam 饰品id
     */
    public Set<String> getMylistings() {
        String url = "https://steamcommunity.com/market/mylistings/render/?query=&start=1&count=100";
        String responseStr = HttpClientUtils.sendGet(url, SteamConfig.getSaleHeader());
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        Integer total_count = jsonObject.getInteger("total_count");
        if (total_count == 0) {
            return new HashSet<>();
        }
        String results_html = jsonObject.getString("results_html");
//        MarketListJsonRootBean marketListJsonRootBean = JSONObject.parseObject(responseStr, MarketListJsonRootBean.class);
//        log.info("123123");
        Document document = Jsoup.parse(results_html);
        Elements market_recent_listing_row = document.getElementsByClass("market_recent_listing_row");
        Set<String> listId = new HashSet<>();
        for (Element element : market_recent_listing_row) {
            String id = element.id();
            listId.add(id.split("_")[1]);
        }
        return listId;
    }

    public void removeList(String id) {
        String url = "https://steamcommunity.com/market/removelisting/" + id;
        Map<String, String> paramerMap = new HashMap<>();
        Map<String, String> saleHeader = SteamConfig.getSaleHeader();
        saleHeader.put("Referer", "https://steamcommunity.com/market/");
        paramerMap.put("sessionid", SteamConfig.getCookieOnlyKey("sessionid"));
      try {
          String responseStr = HttpClientUtils.sendPostForm(url, "", saleHeader, paramerMap);
          log.info("需要审批的饰品下架信息：{}", responseStr);
      }catch (Exception e) {
          e.printStackTrace();
      }
    }
}
