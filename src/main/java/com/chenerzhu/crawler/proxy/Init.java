package com.chenerzhu.crawler.proxy;

import com.chenerzhu.crawler.proxy.buff.ExecutorUtil;
import com.chenerzhu.crawler.proxy.buff.service.ConfirmTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Init implements ApplicationRunner {

    @Autowired
    ConfirmTradeService confirmTradeService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("14123");
        //定时确认收货
        ExecutorUtil.pool.scheduleWithFixedDelay(()->confirmTradeService.SteamTrade(),10,100, TimeUnit.SECONDS);

    }
}
