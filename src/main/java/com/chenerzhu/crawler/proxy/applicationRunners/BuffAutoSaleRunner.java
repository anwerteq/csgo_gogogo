package com.chenerzhu.crawler.proxy.applicationRunners;

import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.buff.service.PullItemService;
import com.chenerzhu.crawler.proxy.buff.service.SteamInventorySerivce;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

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
        PullItemService.executorService.execute(() -> {
            while (true) {
                for (BuffUserData buffUserData : buffUserDataList) {
                    BuffApplicationRunner.buffUserDataThreadLocal.set(buffUserData);
                    CookiesConfig.buffCookies.set(buffUserData.getCookie());
                    log.info("开始下架在售时间久的饰品");
                    steamInventorySerivce.downOnSale();
                    log.info("buff账号：{}开始上架饰品", buffUserData.getAcount());
                    try {
                        autoSale(buffUserData);
                    } catch (HttpClientErrorException e) {
                        log.info("buff账号：{}上架饰品发生429异常:{},切换下一个账号", buffUserData.getAcount(), e);
                        SleepUtil.sleep(20 * 60 * 1000);
                        continue;
                    } catch (Exception e) {
                        log.info("buff账号：{}上架饰品发生异常:{},切换下一个账号", buffUserData.getAcount(), e);
                        continue;
                    }
                    log.info("buff账号：{}上架饰品完成", buffUserData.getAcount());
                }
                log.info("buff账号全部上架完成，睡眠20分钟");
                SleepUtil.sleep(20 * 60 * 1000);

            }
        });
    }

    public void autoSale(BuffUserData buffUserData) {
        try {
            //上架商品
            while (steamInventorySerivce.autoSale()) {

            }
        } catch (Exception e) {
            log.info("buff账号:{},自动上架异常", buffUserData.getAcount(), e);
        }
    }
}
