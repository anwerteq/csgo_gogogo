package com.chenerzhu.crawler.proxy.steam.service;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.ExecutorUtil;
import com.chenerzhu.crawler.proxy.buff.service.ProfitService;
import com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity.BuffBuyItems;
import com.chenerzhu.crawler.proxy.pool.csgo.service.BuffBuyItemService;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.SteamItem;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.SteamSearchRoot;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.SteamSearchdata;
import com.chenerzhu.crawler.proxy.pool.csgo.steamrepostory.SteamItemRepository;
import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
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
    static  int index = 0;



 
    public void pullItems() {
        Map<String, Long> hashnameAndItemId = profitService.selectItemIdANdHashName();
        int start = index;
        int count = 80;
        while (start < 8000) {
            index = start + count;
            try {
                pullItem(start, hashnameAndItemId, count);
            }catch (Exception e){
                log.error("pullItem:异常信息：{}",e);
                return;
            }
            start = start + count;
        }
    }

    /**
     * 拉取具体一页数据
     *
     * @param start
     * @return
     */
 
    public boolean pullItem(int start, Map<String, Long> hashnameAndItemId, int count) {
        String paramer = "&start=" + start + "&count=" + count;
        String itemUrl = "https://steamcommunity.com/market/search/render/?query=&search_descriptions=0&sort_column=popular" +
                "&sort_dir=desc&norender=1" + paramer;
        String rep = HttpClientUtils.sendGet(itemUrl, SteamConfig.getSteamHeader());
        SteamSearchRoot steamSearchRoot = JSONObject.parseObject(rep, SteamSearchRoot.class);
        Boolean isPause = true;
        //保存steam信息
//        ExecutorUtil.pool.execute(() -> saveSteamItems(steamSearchRoot.getResults()));
        for (SteamItem steamItem : steamSearchRoot.getResults()) {
            //steam商品不在推荐的数据上
            if (!hashnameAndItemId.containsKey(steamItem.getHash_name())) {
                continue;
            }
            Long itemId = hashnameAndItemId.get(steamItem.getHash_name());
            //商品在buff的售卖订单
            List<BuffBuyItems> sellOrder = buffBuyItemService.getSellOrder(String.valueOf(itemId));
            if (sellOrder.isEmpty()) {
                continue;
            }
            sellOrder = sellOrder.subList(0, Math.min(3, sellOrder.size()));
            for (BuffBuyItems buffBuyItems : sellOrder) {
                //校验该订单是否购买
                if (profitService.checkBuyItemOrder(buffBuyItems, steamItem.getSell_price())) {
                    buffBuyItems.setName(steamItem.getName());
                    buffBuyItems.setHash_name(steamItem.getHash_name());
                    //校验可以购买该商品的订单
                    ExecutorUtil.pool.execute(() -> buffBuyItemService.createOrderAndPayAndAsk(buffBuyItems));
                }
            }
            isPause = false;
        }
        SleepUtil.sleep(1500);
//        if (isPause) {
//            SleepUtil.sleep(5000);
//        }
        log.info("查询的页数：{},每页的条数：{};", (start / count + 1), count);
        if (start >= steamSearchRoot.getTotal_count()) {
            return false;
        }
        return true;
    }


    
    @Async
    public void saveSteamItems(List<SteamItem> steamItems) {
        steamItems.parallelStream().forEach(steamItem -> {
            steamItemRepository.save(steamItem);
        });
    }
}
