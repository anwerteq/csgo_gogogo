package com.chenerzhu.crawler.proxy.task;

import com.chenerzhu.crawler.proxy.buff.ExecutorUtil;
import com.chenerzhu.crawler.proxy.buff.service.ConfirmTradeService;
import com.chenerzhu.crawler.proxy.buff.service.PullItemService;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
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

    @Autowired
    CookiesConfig cookiesConfig;

    @Autowired
    PullItemService pullItemService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        //定时确认收货
        ExecutorUtil.pool.scheduleWithFixedDelay(() ->{
            try {
                confirmTradeService.steamTradeCookies();
            }catch (Exception e){
                log.error("定时确认收货异常：",e);
            }
        }, 1, 180, TimeUnit.SECONDS);

        //定时获取最新的cookie
        ExecutorUtil.pool.scheduleWithFixedDelay(() ->{
            cookiesConfig.refreshCookie();
        }, 300, 300, TimeUnit.SECONDS);

        //定时重新上架steam上旧的商品
        ExecutorUtil.pool.scheduleWithFixedDelay(() -> {
            try {
                steamItemService.doUpdataPlatformItem();
            }catch (Exception e){
                log.error("商品重新上架异常：",e);

            }
        }, 1, 60 * 20, TimeUnit.SECONDS);

//        //定时拉取steam列表数据,从buff下单
//        ExecutorUtil.pool.scheduleWithFixedDelay(() ->{
//            try {
//                listingsService.pullItems();
//            }catch (Exception e){
//                log.error("定时拉取steam数据异常：",e);
//            }
//        }, 60 *5 , 60 * 30, TimeUnit.SECONDS);


        //        //定时从steam拉取商品数据,从steam下单
        ExecutorUtil.pool.scheduleWithFixedDelay(() ->{
            try {
             pullItemService.pullItmeGoods(true);
            }catch (Exception e){
                log.error("定时拉取steam数据异常：",e);
            }
        }, 60 *5 , 6000 * 30, TimeUnit.SECONDS);
    }
}
