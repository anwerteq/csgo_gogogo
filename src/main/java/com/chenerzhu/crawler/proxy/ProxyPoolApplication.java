package com.chenerzhu.crawler.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ServletComponentScan("com.chenerzhu.crawler.proxy.pool.listener")
@EnableFeignClients
public class ProxyPoolApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ProxyPoolApplication.class, args);

    }
}
