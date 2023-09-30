package com.chenerzhu.crawler.proxy;

import com.chenerzhu.crawler.proxy.buff.service.PullItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan("com.chenerzhu.crawler.proxy.pool.listener")
@EnableFeignClients
@EnableScheduling
@Slf4j
public class ProxyPoolApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ProxyPoolApplication.class, args);
        log.info("开始自动求购steam商品");
        PullItemService bean = run.getBean(PullItemService.class);
        bean.pullItmeGoods(true);
    }
}
