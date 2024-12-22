package com.xiaojuzi;

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
@ServletComponentScan("com.xiaojuzi.pool.listener")
@EnableFeignClients
@EnableScheduling
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
@EntityScan(basePackages = {"com.xiaojuzi.steam.entity", "com.xiaojuzi.csgo.entity"
        ,"com.xiaojuzi.csgo.profitentity","com.xiaojuzi.csgo.steamentity"})

@Slf4j
@EnableCaching
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
