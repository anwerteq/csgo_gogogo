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
    SteamItemRepository steamItemRepository;

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
            while (pullItem(page_index)) {

            }
        });
    }

    /**
     * 拉取具体一页数据
     *
     * @param page_index
     * @return
     */
    public boolean pullItem(int page_index) {
        String itemUrl = "https://steamcommunity.com/market/search/render/?query=&count=100&search_descriptions=0&sort_column=popular&sort_dir=desc&norender=1&start=" + page_index;
        ResponseEntity<String> responseEntity = restTemplate.exchange(itemUrl, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
        if (responseEntity.getStatusCode().value() == 302) {
            return true;
        }
        SteamSearchdata steamSearchdata = JSONObject.parseObject(responseEntity.getBody(), SteamSearchdata.class);
        saveSteamItems(steamSearchdata.getResults());
        log.info("查询的页数：" + page_index);
        if (page_index >= steamSearchdata.getTotal_count()) {
            return false;
        }
        return true;
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

    /**
     * 重新上架久卖的商品
     */
    public void doUpdataPlatformItem(){
        removelistingService.unlistings(1);
        SleepUtil.sleep(1000 * 10);
        groundingService.productListingOperation();
    }

}
