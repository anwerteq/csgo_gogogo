package com.chenerzhu.crawler.proxy.pool.job.scheduler;

import com.chenerzhu.crawler.proxy.pool.entity.AuthorizationKey;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyConfig;
import com.chenerzhu.crawler.proxy.pool.service.IPWhiteListService;
import com.chenerzhu.crawler.proxy.pool.service.IProxyConfigService;
import com.chenerzhu.crawler.proxy.pool.util.MultiDBUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author chenerzhu
 * @create 2018-08-30 10:27
 **/
@Slf4j
@Component
@SuppressWarnings("unchecked")
public class AuthSchedulerJob extends AbstractSchedulerJob {

    @Autowired
    private IProxyConfigService proxyConfigService;
    @Value("${authFlag}")
    private Boolean flag;

    @Override
    public void run() {
        try {
            this.auth();
        }catch (Exception e){
            log.error(e.getMessage(),e);
        } finally {
            shutdown();
        }
    }

    /**
     * 授权验证
     * @return
     */
    public void auth() {
        if(flag){
            ProxyConfig config = proxyConfigService.getConfig();
            if(null != config){

                String sql = "select a.key from authorization_key a where a.is_usable = 1 and a.key = '"+config.getAuth()+"'";
                List<AuthorizationKey> authorizationKeys = MultiDBUtils.getInstance().queryList(sql, AuthorizationKey.class);

                // 验证 无授权自动退出
                if(null == authorizationKeys || authorizationKeys.size() == 0){
                    System.exit(0);
                }
            }
        }
    }
}