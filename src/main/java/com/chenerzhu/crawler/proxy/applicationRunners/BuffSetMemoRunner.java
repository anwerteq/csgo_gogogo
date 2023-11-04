package com.chenerzhu.crawler.proxy.applicationRunners;


import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * buff设置成本
 */
@Slf4j
@Component
@Order(3)
public class BuffSetMemoRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (BuffUserData buffUserData : BuffApplicationRunner.buffUserDataList) {

        }

    }
}
