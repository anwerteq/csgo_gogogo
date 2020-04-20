package com.chenerzhu.crawler.proxy.pool.repository;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author parker
 * @create 2020年4月20日15:23:47
 **/
@Repository
public interface IProxyApiRepository extends JpaRepository<ProxyApi, Long> {

}