package com.xiaojuzi.task;


import com.xiaojuzi.buff.service.PullItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务
 */
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class ScheduleTask {

    @Autowired
    PullItemService pullItemService;


    //3.添加定时任务
//    @Scheduled(cron = "0 0 */6 * * ?") // 每3个小时触发一次
//    private void configureTasks() {
//        log.info("开始定时任务拉取buff数据");
//        pullItemService.pullItmeGoods(false);
//        log.info("定时任务拉取buff数据结束");
//    }
}
