package com.chenerzhu.crawler.proxy.buff.service;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.ExecutorUtil;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.*;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.GoodsInfoRepository;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.IItemGoodsRepository;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 拉取buff 商品数据
 */
@Service
@Slf4j
public class PullItemService {
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    IItemGoodsRepository itemRepository;
    @Autowired
    ProfitService profitService;
    @Autowired
    GoodsInfoRepository goodsInfoRepository;

    @Autowired
    TagService tagService;


    /**
     * 拉取buff商品列表
     */
    public void pullItmeGoods() {
        executorService.execute(() -> {
            int pageIndex = 0;
            while (pullOnePage(++pageIndex)) {
            }
        });
    }


    /**
     * 拉取某一页数据
     * @param pageNum
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean pullOnePage(int pageNum) {
        String url1 = "https://buff.163.com/api/market/goods?game=csgo&page_num=" + pageNum + "&use_suggestion=0&_=1684057330094&page_size=80";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url1, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        if (responseEntity.getStatusCode().value() == 302) {
            return false;
        }
        ProductList productList = JSONObject.parseObject(responseEntity.getBody(), ProductList.class);

        List<ItemGoods> itemGoodsList = productList.getData().getItems();
        itemGoodsList.forEach((item)->{
            ExecutorUtil.pool.execute(()-> saveItem(item));
        });
        log.info("拉取完，第："+ pageNum);

        //是否是最后一页
        if (pageNum >= productList.getData().getTotal_page()) {
            return false;
        }
        return true;
    }


    /**
     * 保存buff的列表信息
     * @param itemGoods
     */

    public void saveItem(ItemGoods itemGoods) {
        itemRepository.save(itemGoods);
        //推荐商品再buff售卖
        profitService.saveSellBuffProfitEntity(itemGoods);
        //推荐商品再buff购买
        profitService. saveSellSteamProfit(itemGoods);
        //保存buff商品信息
        Goods_info goods_info = itemGoods.getGoods_info();
        goods_info.setItem_id(itemGoods.getId());
        goodsInfoRepository.save(goods_info);
        Tags tags = goods_info.getInfo().getTags();
       // tagService.saveTags(tags, itemGoods.getId());
    }




}
