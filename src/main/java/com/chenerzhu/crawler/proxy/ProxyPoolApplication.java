package com.chenerzhu.crawler.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
@Slf4j
@EnableCaching
public class ProxyPoolApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ProxyPoolApplication.class, args);
    }
}
