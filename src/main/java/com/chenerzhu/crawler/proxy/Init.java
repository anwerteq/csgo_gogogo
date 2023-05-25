package com.chenerzhu.crawler.proxy;

import com.chenerzhu.crawler.proxy.buff.ExecutorUtil;
import com.chenerzhu.crawler.proxy.buff.service.ConfirmTradeService;
import com.chenerzhu.crawler.proxy.pool.csgo.service.BuffBuyItemService;
import com.chenerzhu.crawler.proxy.steam.service.SteamItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Init implements ApplicationRunner {

    @Autowired
    ConfirmTradeService confirmTradeService;
    @Autowired
    SteamItemService steamItemService;
    @Autowired
    BuffBuyItemService buffBuyItemService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("14123");
        //定时确认收货
        ExecutorUtil.pool.scheduleWithFixedDelay(() -> confirmTradeService.SteamTrade(), 10, 100, TimeUnit.SECONDS);
        //定时重新上架steam上旧的商品
        ExecutorUtil.pool.scheduleWithFixedDelay(() -> steamItemService.doUpdataPlatformItem(), 300, 600, TimeUnit.SECONDS);
        //定时从buff上购买商品
        ExecutorUtil.pool.scheduleWithFixedDelay(() -> buffBuyItemService.buffSellOrder(), 300, 3600, TimeUnit.SECONDS);

    }
}
