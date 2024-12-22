package com.xiaojuzi.applicationRunners;

import com.xiaojuzi.buff.BuffUserData;
import com.xiaojuzi.buff.service.PullItemService;
import com.xiaojuzi.common.GameCommet;
import com.xiaojuzi.config.CookiesConfig;
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
    private String auto_sale;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (!GameCommet.check(auto_sale)) {
            log.info("配置错误,目前求购脚本只支持：{}", GameCommet.gameMap.keySet());
            return;
        }
        PullItemService.executorService.execute(() -> {
            log.info("开始steam求购游戏：{}商品", auto_sale);
            for (BuffUserData buffUserData : BuffApplicationRunner.buffUserDataList) {
                BuffApplicationRunner.buffUserDataThreadLocal.set(buffUserData);
                CookiesConfig.buffCookies.set(buffUserData.getCookie());
                pullItemService.pullItmeGoods();
            }
        });
    }
}
