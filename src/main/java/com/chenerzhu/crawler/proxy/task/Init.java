package com.chenerzhu.crawler.proxy.task;

import com.chenerzhu.crawler.proxy.buff.ExecutorUtil;
import com.chenerzhu.crawler.proxy.buff.service.ConfirmTradeService;
import com.chenerzhu.crawler.proxy.pool.csgo.service.BuffBuyItemService;
import com.chenerzhu.crawler.proxy.steam.service.ListingsService;
import com.chenerzhu.crawler.proxy.steam.service.SteamItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Init implements ApplicationRunner {

    @Autowired
    ConfirmTradeService confirmTradeService;
    @Autowired
    SteamItemService steamItemService;
    @Autowired
    BuffBuyItemService buffBuyItemService;

    @Autowired
    ListingsService listingsService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        //定时确认收货
//        ExecutorUtil.pool.scheduleWithFixedDelay(() ->{
//            try {
//                confirmTradeService.SteamTrade();
//            }catch (Exception e){
//                log.error("定时确认收货异常：",e);
//            }
//        }, 1, 100, TimeUnit.SECONDS);
        //定时重新上架steam上旧的商品
//        ExecutorUtil.pool.scheduleWithFixedDelay(() -> {
//            try {
//                steamItemService.doUpdataPlatformItem();
//            }catch (Exception e){
//                log.error("商品重新上架异常：",e);
//
//            }
//        }, 1, 3600, TimeUnit.SECONDS);

        //定时拉取steam列表数据,从buff下单
//        ExecutorUtil.pool.scheduleWithFixedDelay(() ->{
//            try {
//                listingsService.pullItems();
//            }catch (Exception e){
//                log.error("定时拉取steam数据异常：",e);
//            }
//        }, 7200, 7200, TimeUnit.SECONDS);
        //定时从buff上购买商品
//        ExecutorUtil.pool.scheduleWithFixedDelay(() -> buffBuyItemService.buffSellOrder(), 3000, 3600, TimeUnit.SECONDS);

    }
}
