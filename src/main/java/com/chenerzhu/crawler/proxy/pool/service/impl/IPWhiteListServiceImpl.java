package com.chenerzhu.crawler.proxy.pool.service.impl;

import com.chenerzhu.crawler.proxy.pool.entity.IPWhiteList;
import com.chenerzhu.crawler.proxy.pool.repository.IPWhiteListRepository;
import com.chenerzhu.crawler.proxy.pool.service.IPWhiteListService;
import com.chenerzhu.crawler.proxy.pool.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author parker
 * @create 2020年4月20日15:25:27
 **/
@Service("whiteListService")
@Slf4j
public class IPWhiteListServiceImpl implements IPWhiteListService {

    /** Redis ip key */
    private static final String REDIS_IP_KEY = "ip_white_list";

    @Autowired
    private IPWhiteListRepository whiteListRepository;


    @Override
    public List<IPWhiteList> findAll() {
        IPWhiteList ipWhiteList = new IPWhiteList();
        ipWhiteList.setId(null);
        ipWhiteList.setIsUsable(1);
        Example<IPWhiteList> example = Example.of(ipWhiteList);
        return whiteListRepository.findAll(example);
    }

    @Override
    public boolean updateIpWhiteList() {
        boolean flag = false;
        try {
            Set<Object> objects = RedisUtil.sGet(REDIS_IP_KEY);
            // 更新白名单1
            List<IPWhiteList> all = this.findAll();
            for (IPWhiteList ipWhiteList : all) {
                // 白名单是否存在 如果不存在就加入
                boolean flag1 = RedisUtil.sHasKey(REDIS_IP_KEY, ipWhiteList.getIp());
                if(!flag1){
                    RedisUtil.sSet(REDIS_IP_KEY,ipWhiteList.getIp());
                }
            }

            // 更新白名单2
            for (Object object : objects) {
                String ip =(String) object;
                boolean flag2 = false;
                for (IPWhiteList ipWhiteList : all) {
                    if(ipWhiteList.getIp().equals(ip)){
                        flag2 = true;
                        break;
                    }
                }

                // 如果当前数据库列表找不到当前 ip 则在redis中 剔除
                if(!flag2){
                    RedisUtil.setRemove(REDIS_IP_KEY,ip);
                }
            }
            flag = true;
        }catch (Exception e){
            flag = false;
            log.error(e.getMessage(),e);
        }

        return flag;
    }

}