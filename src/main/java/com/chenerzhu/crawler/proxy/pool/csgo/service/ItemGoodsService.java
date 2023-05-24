package com.chenerzhu.crawler.proxy.pool.csgo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.*;
import com.chenerzhu.crawler.proxy.pool.csgo.feign.CsgoFeign;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellBuffProfitEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellSteamProfitEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.*;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ItemGoodsService {


    ExecutorService executorService = Executors.newFixedThreadPool(1);


    @Autowired
    IProxyIpRedisService proxyIpRedisService;

    @Autowired
    IItemGoodsRepository itemRepository;

    @Autowired
    GoodsInfoRepository goodsInfoRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    CsgoFeign csgoFeign;

    @Autowired
    RestTemplate restTemplate;

    static AtomicInteger atomicInteger = new AtomicInteger();

    @Autowired
    BuffPriceHistory2Repository history2Repository;

    @Autowired
    BuffPriceHistory1Repository history1Repository;

    @Autowired
    SteamPriceHistoryRepository historyRepository;

    @Autowired
    SellBuffProfitRepository sellBuffProfitRepository;

    @Autowired
    SellSteamProfitRepository sellSteamProfitRepository;

    //总页数
    Integer pageCount = 1;


    /**
     * 拉取csgo商品列表
     */
    public void pullItmeGoods() {


        executorService.execute(()->{
            int pageIndex = 1;
            while (pageCount >= pageIndex) {

                pageIndex = pullOnePage(pageIndex);
                pageIndex++;
                System.out.println("正在查询的页数：" + pageIndex);
                try {
                    Thread.sleep(1000 * 3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


//        pullHistoryPrice();
    }


    @Transactional(rollbackFor = Exception.class)
    public Integer pullOnePage(int pageNum) {
        String url1 = "https://buff.163.com/api/market/goods?game=csgo&page_num=" + pageNum + "&use_suggestion=0&_=1684057330094&page_size=80";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url1, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);

        if (responseEntity.getStatusCode().value() == 302) {
            atomicInteger.addAndGet(1);
            if (atomicInteger.get() > 5) {
                return pageNum;
            }
            pullOnePage(pageNum);
        }
        ProductList productList = JSONObject.parseObject(responseEntity.getBody(), ProductList.class);
        //获取最新页数
        pageCount = productList.getData().getTotal_page();
        List<ItemGoods> itemGoodsList = productList.getData().getItems();

        for (ItemGoods itemGoods : itemGoodsList) {
            saveItem(itemGoods);
        }

        return pageNum;
    }









    @Async
    public void saveItem(ItemGoods itemGoods) {
        itemRepository.save(itemGoods);
        saveSellBuffProfitEntity(itemGoods);
        saveSellSteamProfit(itemGoods);
        Goods_info goods_info = itemGoods.getGoods_info();
        goods_info.setItem_id(itemGoods.getId());
        goodsInfoRepository.save(goods_info);
        Tags tags = goods_info.getInfo().getTags();
        saveTags(tags, itemGoods.getId());
    }

    /**
     * 保存推荐在steam购买的记录
     */
    @Async
    public void saveSellBuffProfitEntity(ItemGoods itemGoods) {
        SellBuffProfitEntity sellBuffProfitEntity = new SellBuffProfitEntity();
        sellBuffProfitEntity.setItem_id(itemGoods.getId());
        sellBuffProfitEntity.setName(itemGoods.getName());
        sellBuffProfitEntity.setSteam_price_cny(itemGoods.getGoods_info().getSteam_price_cny());
        ;
        //购买成本
        double profit = Double.parseDouble(sellBuffProfitEntity.getSteam_price_cny()) * 1.15 * 0.85;
        sellBuffProfitEntity.setIn_fact_steam_price_cny(String.format("%.3f", profit));
        sellBuffProfitEntity.setSell_min_price(itemGoods.getSell_min_price());
        sellBuffProfitEntity.setQuick_price(itemGoods.getQuick_price());
        sellBuffProfitEntity.setSell_num(String.valueOf(itemGoods.getSell_num()));
        double interest = Double.parseDouble(sellBuffProfitEntity.getSell_min_price())
                - Double.parseDouble(sellBuffProfitEntity.getIn_fact_steam_price_cny());
        double interest_rate = (interest / Double.parseDouble(sellBuffProfitEntity.getIn_fact_steam_price_cny()) * 100);
        sellBuffProfitEntity.setInterest_rate(String.format("%.3f", interest_rate));
        Boolean flag = false;
        if (3.0f < interest_rate) {
            //在buff售卖，利率超过3%
            flag = true;
        }
        if (flag) {
            sellBuffProfitRepository.save(sellBuffProfitEntity);
        }
    }

    /**
     * 保存在steam售卖的购买记录
     */
    @Async
    public void saveSellSteamProfit(ItemGoods itemGoods) {
        SellSteamProfitEntity entity = new SellSteamProfitEntity();
        entity.setItem_id(itemGoods.getId());
        entity.setName(itemGoods.getName());
        entity.setBuff_price(itemGoods.getSell_min_price());
        entity.setSell_steam_price(itemGoods.getGoods_info().getSteam_price_cny());
        entity.setSell_num(itemGoods.getSell_num());
        //税后价格
        double in_fact_price = Double.parseDouble(entity.getSell_steam_price()) *
                0.85;
        entity.setIn_fact_sell_steam_price(String.format("%.3f", in_fact_price));
        //buff购买价格
        double buff_price = Double.parseDouble(entity.getBuff_price()) * 1.025;
        entity.setInterest_rate(String.format("%.3f", buff_price / in_fact_price));
        if (0.83 > buff_price / in_fact_price) {
            sellSteamProfitRepository.save(entity);
        }
    }


    @Async
    public void saveTags(Tags tags, long item_id) {
        String tagsStr = JSON.toJSONString(tags);
        HashMap<String, JSONObject> tagsHash = JSON.parseObject(tagsStr, HashMap.class);
        tagsHash.entrySet().parallelStream().forEach(entry -> {
            String tagStr = JSONObject.toJSONString(entry.getValue());
            Tag tag = JSONObject.parseObject(tagStr, Tag.class);
            tag.setItem_id(item_id);
            try {
                tagRepository.save(tag);

            } catch (Exception e) {
                System.out.println("12312");
                throw e;
            }
        });
    }

    public void pullHistoryPrice() {
        //
        String buffHistoryUrl2 = "https://buff.163.com/api/market/goods/price_history?game=csgo&currency=CNY&days=180&buff_price_type=2&_=1684155597514&goods_id=";
        String buffHistoryUrl1 = "https://buff.163.com/api/market/goods/price_history?game=csgo&currency=CNY&days=180&buff_price_type=1&_=1684155597514&goods_id=";
        String steamHistoryUrl = "https://buff.163.com/api/market/goods/price_history?game=csgo&currency=CNY&days=30&buff_price_type=2&_=1684161693973&goods_id=";
        List<Long> itemIds = itemRepository.findAllId();
        long timeMillis = System.currentTimeMillis();
        itemIds.parallelStream().forEach(itemId -> {
            Long lastUpStamp = history2Repository.findlastUpByItemId(itemId);
            if (lastUpStamp == null) {
                lastUpStamp = 0L;
            }
            if (timeMillis < lastUpStamp + 604800000) {
                //7天内更新过
                return;
            }
            System.out.println("输出的itemId:" + itemId);
            ResponseEntity<HistoryPriceRep> responseEntity = restTemplate.exchange(buffHistoryUrl2 + itemId, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), HistoryPriceRep.class);
            if (200 != responseEntity.getStatusCode().value()) {
                return;
            }
            saveBuffHistory2(responseEntity, itemId);
            responseEntity = restTemplate.exchange(buffHistoryUrl1 + itemId, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), HistoryPriceRep.class);
            if (200 != responseEntity.getStatusCode().value()) {
                return;
            }
            saveBuffHistory1(responseEntity, itemId);
            responseEntity = restTemplate.exchange(steamHistoryUrl + itemId, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), HistoryPriceRep.class);
            if (200 != responseEntity.getStatusCode().value()) {
                return;
            }
            saveSteamHistory(responseEntity, itemId);
        });

    }


    /**
     * buff在售最低
     *
     * @param responseEntity
     * @param itemId
     */
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void saveBuffHistory2(ResponseEntity<HistoryPriceRep> responseEntity, long itemId) {
        HistoryPriceRep historyPriceRep = responseEntity.getBody();
        List<List<String>> price_historys = historyPriceRep.getData().getPrice_history();
        List<BuffPriceHistory2> buffPriceHistory2List = new ArrayList<>();
        long upLastTime = System.currentTimeMillis();
        for (List<String> price_history : price_historys) {
            BuffPriceHistory2 buffPriceHistory2 = new BuffPriceHistory2();
            buffPriceHistory2.setItem_id(itemId);
            buffPriceHistory2.setTime_stamp(Long.decode(price_history.get(0)));
            buffPriceHistory2.setPrice(Double.parseDouble(price_history.get(1)));
            buffPriceHistory2.setUp_time_stamp(upLastTime);
            buffPriceHistory2List.add(buffPriceHistory2);
        }
        buffPriceHistory2List.parallelStream().forEach(history2 -> history2Repository.save(history2));
    }


    /**
     * buff成交记录
     *
     * @param responseEntity
     * @param itemId
     */
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void saveBuffHistory1(ResponseEntity<HistoryPriceRep> responseEntity, long itemId) {
        HistoryPriceRep historyPriceRep = responseEntity.getBody();
        List<List<String>> price_historys = historyPriceRep.getData().getPrice_history();
        List<BuffPriceHistory1> buffPriceHistory1List = new ArrayList<>();
        long upLastTime = System.currentTimeMillis();
        for (List<String> price_history : price_historys) {
            BuffPriceHistory1 buffPriceHistory1 = new BuffPriceHistory1();
            buffPriceHistory1.setItem_id(itemId);
            buffPriceHistory1.setTime_stamp(Long.decode(price_history.get(0)));
            buffPriceHistory1.setPrice(Double.parseDouble(price_history.get(1)));
            buffPriceHistory1.setUp_time_stamp(upLastTime);
            buffPriceHistory1List.add(buffPriceHistory1);
        }
        buffPriceHistory1List.parallelStream().forEach(buffPriceHistory1 -> history1Repository.save(buffPriceHistory1));
    }

    /**
     * buff成交记录
     *
     * @param responseEntity
     * @param itemId
     */
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void saveSteamHistory(ResponseEntity<HistoryPriceRep> responseEntity, long itemId) {
        HistoryPriceRep historyPriceRep = responseEntity.getBody();
        List<List<String>> price_historys = historyPriceRep.getData().getPrice_history();
        List<SteamPriceHistory> steamPriceHistories = new ArrayList<>();
        long upLastTime = System.currentTimeMillis();
        for (List<String> price_history : price_historys) {
            SteamPriceHistory steamPriceHistory = new SteamPriceHistory();
            steamPriceHistory.setItem_id(itemId);
            steamPriceHistory.setTime_stamp(Long.decode(price_history.get(0)));
            steamPriceHistory.setPrice(Double.parseDouble(price_history.get(1)));
            steamPriceHistory.setUp_time_stamp(upLastTime);
            steamPriceHistories.add(steamPriceHistory);
        }
        steamPriceHistories.parallelStream().forEach(steamPriceHistory -> historyRepository.save(steamPriceHistory));
    }

    public Page selectHistory1() {
        Pageable pageable = new PageRequest(1, 10);

        Page<BuffPriceHistory1> all = history1Repository.findAll(pageable);
        return all;
    }

}
