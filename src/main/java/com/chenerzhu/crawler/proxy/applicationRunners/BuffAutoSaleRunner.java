package com.chenerzhu.crawler.proxy.applicationRunners;

import com.chenerzhu.crawler.proxy.buff.BuffUserData;
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
        if (!buff_on_the_shelves){
            return;
        }
        try{
            List<BuffUserData> buffUserDataList = BuffApplicationRunner.buffUserDataList;
            for (BuffUserData buffUserData : buffUserDataList) {
                BuffApplicationRunner.buffUserDataThreadLocal.set(buffUserData);
                CookiesConfig.buffCookies.set(buffUserData.getCookie());
                steamInventorySerivce.autoSale();
            }
        }catch (Exception e){

        }

    }
}
