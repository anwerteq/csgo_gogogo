package com.chenerzhu.crawler.proxy;

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
public class ProxyPoolApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ProxyPoolApplication.class, args);

    }
}
