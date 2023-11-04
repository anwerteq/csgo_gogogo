package com.chenerzhu.crawler.proxy.applicationRunners;


import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.service.RemovelistingService;
import com.chenerzhu.crawler.proxy.util.steamlogin.SteamUserDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * steam自动下架功能
 */
@Slf4j
@Component
@Order(3)
public class SteamUnlistingsRunner implements ApplicationRunner {

    @Autowired
    RemovelistingService removelistingService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<SteamUserDate> steamUserDates = SteamApplicationRunner.steamUserDates;
        for (SteamUserDate steamUserDate : steamUserDates) {
            CookiesConfig.steamCookies.set(steamUserDate.getCookies().toString());
            for (int i = 1; i < 100; i++) {
                removelistingService.unlistings(i);
            }
        }

    }
}
