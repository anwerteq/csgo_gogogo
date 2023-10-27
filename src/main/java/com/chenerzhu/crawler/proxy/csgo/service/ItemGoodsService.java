package com.chenerzhu.crawler.proxy.csgo.service;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.applicationRunners.BuffApplicationRunner;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.csgo.service.LowPaintwearEntity.Items;
import com.chenerzhu.crawler.proxy.csgo.service.LowPaintwearEntity.LowPainJsonRootBean;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
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
    public List<String> getLowPaintwear(int page_num){
        List<String> objects = new ArrayList<>();
        String url = "https://buff.163.com/api/market/sell_order/low_paintwear?game=csgo&page_size=500&page_num=" +page_num;
        BuffUserData buffUserData = BuffApplicationRunner.buffUserDataList.get(0);
        CookiesConfig.buffCookies.set(buffUserData.getCookie());
        ResponseEntity<LowPainJsonRootBean> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), LowPainJsonRootBean.class);
        LowPainJsonRootBean body = responseEntity.getBody();
        List<Items> items = body.getData().getItems();
        for (Items item : items) {
            String paintwear = item.getAsset_info().getPaintwear();
            objects.add(paintwear);
        }

        return objects;

    }


    public List<String> getLowPaintwear(){
        List<String> objects = new ArrayList<>();
        for (int i = 1; i < 62; i++) {
           try {
               objects.addAll( getLowPaintwear(i));
               SleepUtil.sleep(10 *1000);
           }catch (Exception e){
               log.error("异常页数：{}",i);
           }
        }

        log.info("魔寻："+ JSONObject.toJSONString(objects));
        return new ArrayList<>();
    }







}
