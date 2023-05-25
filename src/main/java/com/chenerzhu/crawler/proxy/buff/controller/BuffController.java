package com.chenerzhu.crawler.proxy.buff.controller;

import com.chenerzhu.crawler.proxy.buff.service.ConfirmTradeService;
import com.chenerzhu.crawler.proxy.buff.service.PullHistoryService;
import com.chenerzhu.crawler.proxy.buff.service.PullItemService;
import com.chenerzhu.crawler.proxy.pool.csgo.service.BuffBuyItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作buff的控制层
 */
@RestController
@RequestMapping("/buff")
public class BuffController {

    @Autowired
    PullItemService pullItemService;

    @Autowired
    PullHistoryService pullHistoryService;

    @Autowired
    BuffBuyItemService buffBuyItemService;

    @Autowired
    ConfirmTradeService confirmTradeService;

    /**
     * 拉取商品列表信息（拉取推荐购买和销售数据）
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

    /**
     * 购买buff中的商品
     */
    @RequestMapping("buffBuyItems")
    @ResponseBody
    public void buffBuyItems() {
        buffBuyItemService.buffBuyItems();
        System.out.println("接口调用，购买商品完成");
    }


    /**
     * buff确认订单
     */
    @RequestMapping("getSteamTrade")
    @ResponseBody
    public void getSteamTrade() {
        confirmTradeService.SteamTrade();
        System.out.println("接口调用，购买商品完成");
    }
}
