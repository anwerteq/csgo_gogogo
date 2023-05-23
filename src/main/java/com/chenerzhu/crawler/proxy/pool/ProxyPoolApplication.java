package com.chenerzhu.crawler.proxy.pool;

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
//        String proxyHost = "127.0.0.1";
//        String proxyPort = "1080";
//// 对http开启代理
//        System.setProperty("http.proxyHost", proxyHost);
//        System.setProperty("http.proxyPort", proxyPort);
//// 对https也开启代理
//        System.setProperty("https.proxyHost", proxyHost);
//        System.setProperty("https.proxyPort", proxyPort);

        ConfigurableApplicationContext run = SpringApplication.run(ProxyPoolApplication.class, args);
    }
}
