package com.chenerzhu.crawler.proxy.csgo.service;

import com.chenerzhu.crawler.proxy.applicationRunners.BuffApplicationRunner;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.csgo.service.LowPaintwearEntity.LowPainJsonRootBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class ItemGoodsService {


    ExecutorService executorService = Executors.newFixedThreadPool(1);



    @Autowired
    RestTemplate restTemplate;


    /**
     * 获取低磨损数据
     * @param page_num
     */
    public void getLowPaintwear(int page_num){
        String url = "https://buff.163.com/api/market/sell_order/low_paintwear?game=csgo&page_size=500&page_num=" +page_num;
        BuffUserData buffUserData = BuffApplicationRunner.buffUserDataThreadLocal.get();
        CookiesConfig.buffCookies.set(buffUserData.getCookie());
        ResponseEntity<LowPainJsonRootBean> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), LowPainJsonRootBean.class);
        System.out.println("123123");


    }








}
