package com.chenerzhu.crawler.proxy.pool.service;

import com.chenerzhu.crawler.proxy.pool.entity.IPWhiteList;

import java.util.List;

/**
 * Created Date by 2020/4/20 0020.
 *
 * @author Parker
 */
public interface IPWhiteListService {

    /**
     * 获得全部 api接口
     * @return
     */
    List<IPWhiteList> findAll();

    /**
     * 更新白名单
     * @return
     */
    boolean updateIpWhiteList();

}
