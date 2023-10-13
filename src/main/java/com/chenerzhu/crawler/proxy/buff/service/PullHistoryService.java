package com.chenerzhu.crawler.proxy.buff.service;

import cn.hutool.core.date.DateUtil;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.common.GameCommet;
import com.chenerzhu.crawler.proxy.csgo.entity.BuffPriceHistory1;
import com.chenerzhu.crawler.proxy.csgo.entity.BuffPriceHistory2;
import com.chenerzhu.crawler.proxy.csgo.entity.HistoryPriceRep;
import com.chenerzhu.crawler.proxy.csgo.entity.SteamPriceHistory;
import com.chenerzhu.crawler.proxy.csgo.repository.BuffPriceHistory1Repository;
import com.chenerzhu.crawler.proxy.csgo.repository.BuffPriceHistory2Repository;
import com.chenerzhu.crawler.proxy.csgo.repository.IItemGoodsRepository;
import com.chenerzhu.crawler.proxy.csgo.repository.SteamPriceHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PullHistoryService {

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Autowired
    IItemGoodsRepository itemRepository;

    @Autowired
    BuffPriceHistory2Repository history2Repository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    BuffPriceHistory1Repository history1Repository;

    @Autowired
    SteamPriceHistoryRepository historyRepository;

    public static void main(String[] args) {
        // 获取当前日期
        String currentDate = DateUtil.today();
        System.out.println("Current Date: " + currentDate);

        // 获取前20天的日期
        String before20Days = String.valueOf(DateUtil.offsetDay(new Date(), -20).getTime());
        System.out.println("Before 20 Days: " + before20Days);
    }

    /**
     * 拉取历史价格
     */
    public void doPullHistoryPrice() {
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

    public void pullHistoryPrice() {
        executorService.execute(this::doPullHistoryPrice);
    }

    /**
     * 获取itemId近20天中位数
     *
     * @return
     */
    public Double get20dayMedianPrice(String itemId, int day) {
        String buffHistoryUrl2 = "https://buff.163.com/api/market/goods/price_history/buff?game=" + GameCommet.getGame() + "&currency=CNY" +
                "&buff_price_type=2&_=" + System.currentTimeMillis() + "&days=" + day + "&goods_id=" + itemId;
        ResponseEntity<HistoryPriceRep> responseEntity = restTemplate.exchange(buffHistoryUrl2, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), HistoryPriceRep.class);
        if (200 != responseEntity.getStatusCode().value()) {
            return 0.0;
        }
        HistoryPriceRep historyPriceRep = responseEntity.getBody();
        List<List<String>> price_historys = historyPriceRep.getData().getPrice_history();
        //前20天的时间戳
        long beforeDate = DateUtil.offsetDay(new Date(), -day).getTime();
        List<List<String>> before20ddayHistorys = price_historys.stream()
                .filter(price_history -> beforeDate < Long.valueOf(price_history.get(0))).collect(Collectors.toList());
        List<String> sellListPrices = before20ddayHistorys.stream().map(history -> history.get(1)).collect(Collectors.toList());
        Collections.sort(sellListPrices);
        String medianPrice = sellListPrices.get(Integer.valueOf(sellListPrices.size() / 2));
        return Double.valueOf(medianPrice);
    }

    /**
     * buff在售最低
     *
     * @param responseEntity
     * @param itemId
     */

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
     * steam价格走势
     *
     * @param responseEntity
     * @param itemId
     */

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


}
