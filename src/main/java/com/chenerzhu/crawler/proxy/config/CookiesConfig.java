package com.chenerzhu.crawler.proxy.config;


import com.chenerzhu.crawler.proxy.steam.entity.Cookeis;
import com.chenerzhu.crawler.proxy.steam.repository.CookeisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CookiesConfig  implements ApplicationRunner {


    public static ThreadLocal<String> buffCookies = new ThreadLocal<>();
    public static ThreadLocal<String> steamCookies = new ThreadLocal<>();
    @Autowired
    CookeisRepository cookeisRepository;

    /**
     * 全部账号的cookies信息
     */
    public static List<Cookeis> cookeisList = new ArrayList<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        cookeisList = cookeisRepository.findAll();
    }
}
