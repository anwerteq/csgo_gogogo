package com.xiaojuzi.st.steam.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.xiaojuzi.st.applicationRunners.SteamApplicationRunner;
import com.xiaojuzi.st.steam.SteamConfig;
import com.xiaojuzi.st.steam.util.SleepUtil;
import com.xiaojuzi.st.util.HttpClientUtils;
import com.xiaojuzi.st.util.steamlogin.SteamUserDate;
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
        mylistings.forEach(this::removeList);
//
//        String sessionid = resStr.split("g_sessionID = \"")[1].split(";")[0];
//        sessionid = sessionid.substring(0, sessionid.length() - 1);
//        SteamUserDate steamUserDate = SteamApplicationRunner.steamUserDateTL.get();
//        steamUserDate.getSession().setSessionID(sessionid);


//        String url = "https://steamcommunity.com/market";
//        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
//        Document parse = Jsoup.parse(resStr);
//
//        Element marketListingsRows = parse.getElementById("tabContentsMyActiveMarketListingsRows");
//        //下架，已经上架的商品
//        parseActiveMarketList(marketListingsRows);
//        Elements market_content_block = parse.getElementsByClass("my_listing_section market_content_block market_home_listing_table");
//        //取消需要审核的商品
//        parseMarkBlockList(market_content_block);
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
        SteamUserDate steamUserDate = SteamApplicationRunner.steamUserDateTL.get();
//        paramerMap.put("sessionid", steamUserDate.getSession().getSessionID());
        paramerMap.put("sessionid", SteamConfig.getCookieOnlyKey("sessionid"));
        String responseStr = HttpClientUtils.sendPostForm(url, "", saleHeader, paramerMap);
        ArrayList arrayList = JSONObject.parseObject(responseStr, ArrayList.class);
//        SleepUtil.sleep(500);
        log.info("需要审批的饰品下架信息：{}", responseStr);
    }
}
