package com.xiaojuzi.applicationRunners;


import com.xiaojuzi.buff.service.BuffSetMemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Autowired
    BuffSetMemoService buffSetMemoService;

    @Value("${auto_remark}")
    private Boolean auto_remark;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        if (!auto_remark){
//            return;
//        }
//        PullItemService.executorService.execute(() -> {
//            for (BuffUserData buffUserData : BuffApplicationRunner.buffUserDataList) {
//                BuffApplicationRunner.buffUserDataThreadLocal.set(buffUserData);
//                buffSetMemoService.assetRemarkChange();
//            }
//        });

    }
}
