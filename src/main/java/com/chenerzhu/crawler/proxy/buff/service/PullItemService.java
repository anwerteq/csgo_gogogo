package com.chenerzhu.crawler.proxy.buff.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.csgo.entity.ItemGoods;
import com.chenerzhu.crawler.proxy.csgo.entity.ProductList;
import com.chenerzhu.crawler.proxy.csgo.repository.GoodsInfoRepository;
import com.chenerzhu.crawler.proxy.csgo.repository.IItemGoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


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
     * @param isBuy：trueL：steam购买，false不购买
     */
    public void pullItmeGoods(Boolean isBuy) {
//        pullOnePage(12);
        executorService.execute(() -> {

            String[] ass = new String[]{"smg", "hands", "rifle", "pistol", "shotgun", "machinegun"};
            List<String> types = Arrays.stream(ass).collect(Collectors.toList());
            for (String category_group : types) {
                AtomicInteger atomicInteger = new AtomicInteger(1);
                try{
                    while (pullOnePage(atomicInteger,isBuy,category_group)) {
                        atomicInteger.addAndGet(1);
                    }
                }catch (Exception e){
                    log.error("异常",e);
                }
            }

        });
    }


    /**
     * 拉取某一页数据
     * @param atomicInteger
     * @param isBuy
     * @param category_group :装备分类参数
     * @return
     */

    public Boolean pullOnePage(AtomicInteger atomicInteger,Boolean isBuy,String category_group) {
        String url1 = "https://buff.163.com/api/market/goods?game=csgo&page_num=" + atomicInteger.get()
        + "&use_suggestion=0&_=1684057330094&page_size=80&max_price=100";//&sort_by=sell_num.des
        if (StrUtil.isNotEmpty(category_group)){
            url1 = url1 + "&category_group=" + category_group;
        }
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ResponseEntity<String> responseEntity = restTemplate.exchange(url1, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        if (responseEntity.getStatusCode().value() == 302) {
            return false;
        }
        ProductList productList = JSONObject.parseObject(responseEntity.getBody(), ProductList.class);
        if ("Login Required".equals(productList.getCode())){
            log.error("buff的coookie过期，请在配置文件修改buff的cookie信息");
            throw new ArithmeticException("");
        }
        List<ItemGoods> itemGoodsList = productList.getData().getItems();
//        itemGoodsList.parallelStream().forEach(item -> saveItem(item,isBuy));
        itemGoodsList.forEach((item)->{
            saveItem(item,isBuy);
        });
        log.info("拉取完，第："+ atomicInteger.get());
        //是否是最后一页
        return atomicInteger.get() < productList.getData().getTotal_page();
    }


    /**
     * 保存buff的列表信息
     * @param itemGoods
     */

    public void saveItem(ItemGoods itemGoods,Boolean isBuy) {
//        itemRepository.save(itemGoods);
        //推荐商品在buff售卖
        profitService.saveSellBuffProfitEntity(itemGoods,isBuy);
        //推荐商品再buff购买
//        profitService. saveSellSteamProfit(itemGoods);
//        //保存buff商品信息
//        Goods_info goods_info = itemGoods.getGoods_info();
//        goods_info.setItem_id(itemGoods.getId());
//        goodsInfoRepository.save(goods_info);
//        Tags tags = goods_info.getInfo().getTags();
       // tagService.saveTags(tags, itemGoods.getId());
    }


    public void test() {
    }
}
