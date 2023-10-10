package com.chenerzhu.crawler.proxy.applicationRunners;

import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.buff.service.SteamInventorySerivce;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * buff自动上架
 */
@Slf4j
@Component
@Order(3)
public class BuffAutoSaleRunner implements ApplicationRunner {

    @Autowired
    SteamInventorySerivce steamInventorySerivce;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<BuffUserData> buffUserDataList = BuffApplicationRunner.buffUserDataList;
        for (BuffUserData buffUserData : buffUserDataList) {
            BuffApplicationRunner.buffUserDataThreadLocal.set(buffUserData);
            CookiesConfig.buffCookies.set(buffUserData.getCookie());
            steamInventorySerivce.autoSale();
        }

    }
}
