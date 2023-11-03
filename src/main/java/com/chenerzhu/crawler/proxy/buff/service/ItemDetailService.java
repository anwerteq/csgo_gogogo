package com.chenerzhu.crawler.proxy.buff.service;


import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * buff商品明细服务
 */
@Service
@Slf4j
public class ItemDetailService {


    @Autowired
    RestTemplate restTemplate;

    /**
     * 获取这个商品的磨损区间
     *
     * @param goodId
     * @return
     */
    public List<String> getWearInterval(String goodId) {
        String url = "https://buff.163.com/goods/" + goodId + "?from=market";
        SleepUtil.sleep(5000);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        if (responseEntity.getStatusCode().value() == 302) {
            return null;
        }
        String body = responseEntity.getBody();
        try {
            String wearStr  = body.split(" paintwear_choices: ")[1].split(",\n" +
                    "             ")[0];
            System.out.println("123123123");
        }catch (Exception e){
            log.error("获取饰品的磨损区间失败",e);
        }
        return new ArrayList<>();
    }
}
