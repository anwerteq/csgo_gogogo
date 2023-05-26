package com.chenerzhu.crawler.proxy.steam.service;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.ExecutorUtil;
import com.chenerzhu.crawler.proxy.buff.service.ProfitService;
import com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity.BuffBuyItems;
import com.chenerzhu.crawler.proxy.pool.csgo.service.BuffBuyItemService;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.SteamItem;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.SteamSearchdata;
import com.chenerzhu.crawler.proxy.pool.csgo.steamrepostory.SteamItemRepository;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * steam商品清单
 */
@Service
@Slf4j
public class ListingsService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SteamItemRepository steamItemRepository;

    @Autowired
    ProfitService profitService;

    @Autowired
    BuffBuyItemService buffBuyItemService;


    public void pullItems() {
        Map<String, Long> hashnameAndItemId = profitService.selectItemIdANdHashName();
        int start = 0;
        int count = 10;
        while (start < 4000) {
            pullItem(start, hashnameAndItemId);
            start = start + count;
        }
    }

    /**
     * 拉取具体一页数据
     *
     * @param start
     * @return
     */
    public boolean pullItem(int start, Map<String, Long> hashnameAndItemId) {
        String itemUrl = "https://steamcommunity.com/market/search/render/?query=&count=100&search_descriptions=0&sort_column=popular&sort_dir=desc&norender=1&start=" + start;
        ResponseEntity<String> responseEntity = restTemplate.exchange(itemUrl, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
        if (responseEntity.getStatusCode().value() == 302) {
            return true;
        }
        SteamSearchdata steamSearchdata = JSONObject.parseObject(responseEntity.getBody(), SteamSearchdata.class);
        Boolean isPause = true;
        //保存steam信息
        ExecutorUtil.pool.execute(() -> saveSteamItems(steamSearchdata.getResults()));
        for (SteamItem steamItem : steamSearchdata.getResults()) {
            //steam商品不在推荐的数据上
            if (!hashnameAndItemId.containsKey(steamItem.getHash_name())) {
                continue;
            }
            Long itemId = hashnameAndItemId.get(steamItem.getHash_name());
            //该商品在buff的订单
            List<BuffBuyItems> sellOrder = buffBuyItemService.getSellOrder(String.valueOf(itemId));
            if (sellOrder.isEmpty()) {
                continue;
            }
            sellOrder = sellOrder.subList(0, Math.min(3, sellOrder.size()));
            for (BuffBuyItems buffBuyItems : sellOrder) {
                //校验该订单是否购买
                if (profitService.checkBuyItemOrder(buffBuyItems, steamItem.getSell_price())) {
                    //校验可以购买该商品的订单
                    ExecutorUtil.pool.execute(() -> buffBuyItemService.createOrderAndPayAndAsk(buffBuyItems));
                }
            }
            isPause = false;
        }
        if (isPause) {
            SleepUtil.sleep(1000);
        }

        log.info("查询的页数：" + start);
        if (start >= steamSearchdata.getTotal_count()) {
            return false;
        }
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    @Async
    public void saveSteamItems(List<SteamItem> steamItems) {
        steamItems.parallelStream().forEach(steamItem -> {
            steamItemRepository.save(steamItem);
        });
    }
}
