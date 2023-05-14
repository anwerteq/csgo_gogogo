package com.chenerzhu.crawler.proxy.pool.csgo.service;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.csgo.constant.CsgoUrlConstant;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.Item;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.ProductList;
import com.chenerzhu.crawler.proxy.pool.csgo.feign.CsgoFeign;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.IItemRepository;
import com.chenerzhu.crawler.proxy.pool.csgo.util.HttpsSendUtil;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ItemService {


    @Autowired
    HttpsSendUtil httpsSendUtil;

    @Autowired
    IProxyIpRedisService proxyIpRedisService;

    @Autowired
    IItemRepository itemRepository;

    @Autowired
    CsgoFeign csgoFeign;

    @Autowired
    RestTemplate restTemplate;

    static AtomicInteger atomicInteger = new AtomicInteger();

    //csgo请求头数据
    static Map<String, String> map = new HashMap() {
        {
            put("sec-ch-ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Google Chrome\";v=\"114\"");
            put("Accept", "application/json, text/javascript, */*; q=0.07");
            put("X-Requested-With", "XMLHttpRequest");
            put("sec-ch-ua-mobile", "?0");
            put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
            put("sec-ch-ua-platform", "\"Windows\"");
            put("Sec-Fetch-Site", "same-origin");
            put("Sec-Fetch-Mode", "cors");
            put("Sec-Fetch-Dest", "empty");
            put("Referer", "http://buff.163.com/market/csgo");
//                put("Cookie", "Device-Id=8qNWoqIr0y4cISb0LBIS; client_id=YlNlv5mS1lJdkqcidGXcwQ; csrf_token=IjVjZWYzODRmMzU0NjBhZDEyMDc4NmE4ODc1OTMwMzQ5YzEzNzg2OTgi.F0IlFA.CqSOXlLH17C7l9v8Zl8GC2NZHew");
        }
    };

    /**
     * 拉取csgo商品列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void pullItem() {

        ProductList productList = csgoFeign.getItems("1");


        HttpHeaders headers1 = new HttpHeaders();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            headers1.set(entry.getKey(), entry.getValue());
        }
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity<>(headers1);
        String url1 = "https://buff.163.com/api/market/goods?game=csgo&page_num=1&use_suggestion=0&_=1684057330094";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url1, HttpMethod.GET, entity1, String.class);

        if (responseEntity.getStatusCode().value() == 302) {
//            for (Map.Entry<String, List<String>> entry : responseEntity.getHeaders().entrySet()) {
//                map.put(entry.getKey(), String.valueOf(entry.getValue()));
//       8     }
            atomicInteger.addAndGet(1);
            if (atomicInteger.get() > 5) {
                return;
            }
            pullItem();
        }
        productList = JSONObject.parseObject(responseEntity.getBody(), ProductList.class);
        System.out.println("123123");
        List<Item> items = productList.getData().getItems();

        for (Item item : items) {
            itemRepository.save(item);
        }

    }
}
