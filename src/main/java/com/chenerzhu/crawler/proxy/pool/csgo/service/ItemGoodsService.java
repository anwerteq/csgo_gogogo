package com.chenerzhu.crawler.proxy.pool.csgo.service;

import com.chenerzhu.crawler.proxy.pool.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.*;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.*;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ItemGoodsService {


    ExecutorService executorService = Executors.newFixedThreadPool(1);


    @Autowired
    IProxyIpRedisService proxyIpRedisService;

    @Autowired
    IItemGoodsRepository itemRepository;

    @Autowired
    GoodsInfoRepository goodsInfoRepository;

    @Autowired
    TagRepository tagRepository;




    static AtomicInteger atomicInteger = new AtomicInteger();

    @Autowired
    BuffPriceHistory2Repository history2Repository;

    @Autowired
    BuffPriceHistory1Repository history1Repository;

    @Autowired
    SteamPriceHistoryRepository historyRepository;

    @Autowired
    SellBuffProfitRepository sellBuffProfitRepository;

    @Autowired
    SellSteamProfitRepository sellSteamProfitRepository;








}
