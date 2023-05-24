package com.chenerzhu.crawler.proxy.pool.csgo.controller;

import com.chenerzhu.crawler.proxy.buff.service.PullHistoryService;
import com.chenerzhu.crawler.proxy.buff.service.PullItemService;
import com.chenerzhu.crawler.proxy.pool.controller.BaseController;
import com.chenerzhu.crawler.proxy.pool.csgo.service.BuffBuyItemService;
import com.chenerzhu.crawler.proxy.pool.csgo.service.ItemGoodsService;
import com.chenerzhu.crawler.proxy.pool.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author chenerzhu
 * @create 2018-08-29 19:51
 **/
@Slf4j
@Controller
@RequestMapping("item")
public class ItemController extends BaseController {

    @Autowired
    private ItemGoodsService itemGoodsService;


    @Autowired
    BuffBuyItemService buffBuyItemService;

    @Autowired
    ProxyUtils proxyUtils;

    @Autowired
    PullItemService pullItemService;

    @Autowired
    PullHistoryService pullHistoryService;

    /**
     * 拉取商品简要信息
     */
    @RequestMapping("pullItmeGoods")
    @ResponseBody
    public void pullItem() {
        pullItemService.pullItmeGoods();
    }


    /**
     * 拉取商品的历史交易价格
     */
    @RequestMapping("pullHistoryPrice")
    @ResponseBody
    public void pullHistoryPrice() {
        pullHistoryService.pullHistoryPrice();
    }




}
