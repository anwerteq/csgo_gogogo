package com.chenerzhu.crawler.proxy.pool.service;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyApi;

import java.util.List;

/**
 * Created Date by 2020/4/20 0020.
 *
 * @author Parker
 */
public interface IProxyApiService {

    /**
     * 获得全部 api接口
     * @return
     */
    List<ProxyApi> findAll();

}
