package com.chenerzhu.crawler.proxy.applicationRunners;

import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.buff.service.NoticeService;
import com.chenerzhu.crawler.proxy.buff.service.PullItemService;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 自动接收buff的报价和steam确认报价
 */
@Slf4j
@Component
@Order(3)
public class BuffAutoTraderRunner implements ApplicationRunner {

    @Autowired
    NoticeService noticeService;
    @Value("${auto_trader}")
    private Boolean auto_trader;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!auto_trader) {
            return;
        }
        PullItemService.executorService.execute(() -> {
            log.info("开始buff自动收货");
            for (BuffUserData buffUserData : BuffApplicationRunner.buffUserDataList) {
                BuffApplicationRunner.buffUserDataThreadLocal.set(buffUserData);
                CookiesConfig.buffCookies.set(buffUserData.getCookie());
                noticeService.steamTrade();
            }
        });
    }
}
