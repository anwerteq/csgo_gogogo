package com.chenerzhu.crawler.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan("com.chenerzhu.crawler.proxy.pool.listener")
@EnableFeignClients
@EnableScheduling
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
@EntityScan(basePackages = {"com.chenerzhu.crawler.proxy.steam.entity", "com.chenerzhu.crawler.proxy.csgo.entity"
        ,"com.chenerzhu.crawler.proxy.csgo.profitentity","com.chenerzhu.crawler.proxy.csgo.steamentity"})

@Slf4j
@EnableCaching
public class ProxyPoolApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ProxyPoolApplication.class, args);
//        SteamLossPaintwearService bean = run.getBean(SteamLossPaintwearService.class);
//        bean.getMarketLists(null,null);
//        ItemGoodsService bean = run.getBean(ItemGoodsService.class);
//        bean.getLowPaintwear();
    }
}
