package com.chenerzhu.crawler.proxy.pool.service;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyConfig;

/**
 * Created Date by 2020/4/20 0020.
 *
 * @author Parker
 */
public interface IProxyConfigService {

    /**
     * 获得全部 api接口
     * @return
     */
    ProxyConfig getConfig();

}
