package com.chenerzhu.crawler.proxy.pool.job.scheduler;

import com.chenerzhu.crawler.proxy.pool.service.IPWhiteListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author chenerzhu
 * @create 2018-08-30 10:27
 **/
@Slf4j
@Component
@SuppressWarnings("unchecked")
public class UpdateWhiteListSchedulerJob extends AbstractSchedulerJob {

    @Autowired
    private IPWhiteListService whiteListService;

    @Override
    public void run() {
        try {
            this.updateIPWhiteList();
        }catch (Exception e){
            log.error(e.getMessage(),e);
        } finally {
            shutdown();
        }
    }

    /**
     * 更新白名单
     * @return
     */
    public boolean updateIPWhiteList() {
        return whiteListService.updateIpWhiteList();
    }
}