package com.chenerzhu.crawler.proxy.pool.job.scheduler;

import com.chenerzhu.crawler.proxy.pool.entity.AuthorizationKey;
import com.chenerzhu.crawler.proxy.pool.job.execute.ISchedulerJobExecutor;
import com.chenerzhu.crawler.proxy.pool.job.execute.impl.SchedulerJobExecutor;
import com.chenerzhu.crawler.proxy.pool.util.MultiDBUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author chenerzhu
 * @create 2018-09-21 15:03
 **/
@Slf4j
@Component
public class SchedulerJob implements Runnable {
    private static ISchedulerJobExecutor schedulerJobExecutor = new SchedulerJobExecutor(10, "schedulerJob");
    @Resource
    @Qualifier("syncDbSchedulerJob")
    private AbstractSchedulerJob syncDbSchedulerJob;
    @Resource
    @Qualifier("syncRedisSchedulerJob")
    private AbstractSchedulerJob syncRedisSchedulerJob;
    @Resource
    @Qualifier("validateRedisSchedulerJob")
    private AbstractSchedulerJob validateRedisSchedulerJob;
    @Resource
    @Qualifier("updateWhiteListSchedulerJob")
    private AbstractSchedulerJob updateWhiteListSchedulerJob;
    @Resource
    @Qualifier("authSchedulerJob")
    private AbstractSchedulerJob authSchedulerJob;
    @Autowired
    private AuthSchedulerJob authSchedulerJobService;

    @Override
    public void run() {
        try{
            // 默认授权验证
            authSchedulerJobService.auth();
            // 定时授权验证
            schedulerJobExecutor.execute(authSchedulerJob,90,30, TimeUnit.SECONDS);
            // 更新白名单
            schedulerJobExecutor.execute(updateWhiteListSchedulerJob,60,30, TimeUnit.SECONDS);

            schedulerJobExecutor.execute(syncDbSchedulerJob,10, 5, TimeUnit.SECONDS);
            schedulerJobExecutor.execute(syncRedisSchedulerJob,50, 30, TimeUnit.SECONDS);
            schedulerJobExecutor.execute(validateRedisSchedulerJob,100, 30, TimeUnit.SECONDS);
        }catch (Exception e){
            log.error("schedulerJob error:{}",e);
            schedulerJobExecutor.shutdown();
        }finally {

        }
    }
}
