package com.chenerzhu.crawler.proxy.pool.service.impl;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyApi;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.repository.IProxyApiRepository;
import com.chenerzhu.crawler.proxy.pool.repository.IProxyIpRepository;
import com.chenerzhu.crawler.proxy.pool.service.IProxyApiService;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpService;
import com.chenerzhu.crawler.proxy.pool.util.ProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author parker
 * @create 2020年4月20日15:25:27
 **/
@Service("proxyApiService")
public class ProxyApiServiceImpl implements IProxyApiService {

    @Autowired
    private IProxyApiRepository proxyApiRepository;


    @Override
    public List<ProxyApi> findAll() {
        return proxyApiRepository.findAll();
    }

}