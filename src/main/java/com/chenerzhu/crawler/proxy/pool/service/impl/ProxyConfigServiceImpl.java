package com.chenerzhu.crawler.proxy.pool.service.impl;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyConfig;
import com.chenerzhu.crawler.proxy.pool.repository.IProxyConfigRepository;
import com.chenerzhu.crawler.proxy.pool.service.IProxyConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author parker
 * @create 2020年4月20日15:25:27
 **/
@Service("proxyConfigService")
public class ProxyConfigServiceImpl implements IProxyConfigService {

    @Autowired
    private IProxyConfigRepository proxyConfigRepository;


    @Override
    public ProxyConfig getConfig() {
        List<ProxyConfig> all = proxyConfigRepository.findAll();
        if(null != all && !all.isEmpty()){
            return all.get(0);
        }
        return null;
    }
}