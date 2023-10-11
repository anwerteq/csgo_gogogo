package com.chenerzhu.crawler.proxy.applicationRunners;

import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.buff.service.PullItemService;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@Order(3)
public class SteamautoBuyRunner implements ApplicationRunner {

    @Autowired
    PullItemService pullItemService;

    @Value("${auto_sale}")
    private Boolean auto_sale;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!auto_sale) {
            return;
        }
        PullItemService.executorService.execute(() -> {
            log.info("开始steam求购商品");
            for (BuffUserData buffUserData : BuffApplicationRunner.buffUserDataList) {
                BuffApplicationRunner.buffUserDataThreadLocal.set(buffUserData);
                CookiesConfig.buffCookies.set(buffUserData.getCookie());
                pullItemService.pullItmeGoods();
            }
        });
    }
}
