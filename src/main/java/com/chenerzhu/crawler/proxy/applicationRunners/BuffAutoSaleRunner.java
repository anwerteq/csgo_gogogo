package com.chenerzhu.crawler.proxy.applicationRunners;

import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.buff.service.PullItemService;
import com.chenerzhu.crawler.proxy.buff.service.SteamInventorySerivce;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${buff_on_the_shelves}")
    private Boolean buff_on_the_shelves;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!buff_on_the_shelves) {
            return;
        }
        List<BuffUserData> buffUserDataList = BuffApplicationRunner.buffUserDataList;
        for (BuffUserData buffUserData : buffUserDataList) {
            PullItemService.executorService.execute(() -> {
                BuffApplicationRunner.buffUserDataThreadLocal.set(buffUserData);
                CookiesConfig.buffCookies.set(buffUserData.getCookie());
                autoSale(buffUserData);
            });
        }
    }

    public void autoSale(BuffUserData buffUserData) {
        while (true) {
            log.info("buff账号:{},开始自动上架", buffUserData.getAcount());
            try {
                //上架商品
                steamInventorySerivce.autoSale();
                //下架没有磨损度的商品
                steamInventorySerivce.downOnSale();
            } catch (Exception e) {
                log.info("buff账号:{},自动上架异常", buffUserData.getAcount(), e);
            }
            log.info("buff账号:{},开始睡眠30s，避免ip频繁访问", buffUserData.getAcount());
//            SleepUtil.sleep(30 * 1000);
        }
    }
}
