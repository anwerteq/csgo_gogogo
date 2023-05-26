package com.chenerzhu.crawler.proxy.steam.service;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.AssetDescription;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.SteamItem;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.SteamSearchdata;
import com.chenerzhu.crawler.proxy.pool.csgo.steamrepostory.SteamItemRepository;
import com.chenerzhu.crawler.proxy.pool.csgo.steamrepostory.SteamtDescriptionRepository;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * steam服务实体类
 */
@Service
@Slf4j
public class SteamItemService {


    @Autowired
    RestTemplate restTemplate;



    @Autowired
    SteamtDescriptionRepository descriptionRepository;

    @Autowired
    RemovelistingService removelistingService;

    @Autowired
    GroundingService groundingService;

    ExecutorService executorService = Executors.newFixedThreadPool(1);


    public void pullItems() {
        executorService.execute(() -> {
            int page_index = 0;
//            while (pullItem(page_index)) {
//
//            }
        });
    }




    @Transactional(rollbackFor = Exception.class)
    @Async
    public void saveDescriptionRepository(AssetDescription assetDescription) {
        descriptionRepository.save(assetDescription);
    }

    /**
     * 重新上架久卖的商品
     */
    public void doUpdataPlatformItem(){
        removelistingService.unlistings(1);
        SleepUtil.sleep(1000 * 10);
        groundingService.productListingOperation();
    }

}
