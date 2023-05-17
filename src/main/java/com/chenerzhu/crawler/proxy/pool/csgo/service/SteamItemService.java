package com.chenerzhu.crawler.proxy.pool.csgo.service;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.AssetDescription;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.SteamItem;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.SteamSearchdata;
import com.chenerzhu.crawler.proxy.pool.csgo.steamrepostory.SteamItemRepository;
import com.chenerzhu.crawler.proxy.pool.csgo.steamrepostory.SteamtDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * steam服务实体类
 */
@Service
public class SteamItemService {

    static AtomicInteger atomicInteger = new AtomicInteger();

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SteamItemRepository steamItemRepository;

    @Autowired
    SteamtDescriptionRepository descriptionRepository;

    int searchPagecount = 0;

    public void pullItems() {
        int start = 0;
        //调用第一次，拿总条数
        pullItem(start);
        int pageNo =  searchPagecount/100;
       List<Integer> pageNoList = new ArrayList<>();
        for (int i = 1; i <= pageNo; i++) {
            pageNoList.add(i);
        }
        pageNoList.parallelStream().forEach(pageIndex -> pullItem(pageIndex));
    }

    /**
     * 拉取具体一页数据
     * @param start
     * @return
     */
    public int pullItem(int start) {
        String itemUrl = "https://steamcommunity.com/market/search/render/?query=&start=0&count=100&search_descriptions=0&sort_column=popular&sort_dir=desc&norender=1";

        ResponseEntity<String> responseEntity = restTemplate.exchange(itemUrl, HttpMethod.GET, getHttpEntity(), String.class);
        if (responseEntity.getStatusCode().value() == 302) {
            atomicInteger.addAndGet(1);
            if (atomicInteger.get() > 5) {
                return start;
            }
        }
        atomicInteger.set(0);
        SteamSearchdata steamSearchdata = JSONObject.parseObject(responseEntity.getBody(), SteamSearchdata.class);
        searchPagecount = steamSearchdata.getTotal_count();
        List<SteamItem> results = steamSearchdata.getResults();
        saveSteamItems(results);
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Async
    public void saveSteamItems(List<SteamItem> steamItems) {
        steamItems.parallelStream().forEach(steamItem -> {
            steamItemRepository.save(steamItem);
            saveDescriptionRepository(steamItem.getAsset_description());
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Async
    public void saveDescriptionRepository(AssetDescription assetDescription) {
        descriptionRepository.save(assetDescription);
    }

    public HttpEntity<MultiValueMap<String, String>> getHttpEntity() {
        HttpHeaders headers1 = new HttpHeaders();

        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity<>(headers1);
        return entity1;
    }
}
