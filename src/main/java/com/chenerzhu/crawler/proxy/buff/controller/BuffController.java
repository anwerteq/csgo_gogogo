package com.chenerzhu.crawler.proxy.buff.controller;

import com.chenerzhu.crawler.proxy.buff.service.*;
import com.chenerzhu.crawler.proxy.pool.csgo.service.BuffBuyItemService;
import com.chenerzhu.crawler.proxy.steam.service.SteamBuyItemService;
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

    @Autowired
    SteamBuyItemService steamBuyItemService;

    @Autowired
    SteamInventorySerivce steamInventorySerivce;

    @Autowired
    OrderHistoryService orderHistoryService;

    /**
     * 拉取商品列表信息（拉取推荐购买和销售数据）
     */
    @RequestMapping("pullItmeGoods")
    @ResponseBody
    public void pullItem() {
        pullItemService.pullItmeGoods(false);
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
     * 购买buff中热门产品
     */
    @RequestMapping("buyHotItem")
    @ResponseBody
    public void buyHotItem() {
        buffBuyItemService.buyHotItem();
        System.out.println("接口调用，购买商品完成");
    }
    /**
     * buff确认订单
     */
    @RequestMapping("getSteamTrade")
    @ResponseBody
    public void getSteamTrade() {
        confirmTradeService.steamTradeCookies();
        System.out.println("接口调用，buff确认收货完成");
    }


    /**
     * buff自动上架
     */
    @RequestMapping("steamInventory")
    @ResponseBody
    public void steamInventory() {
        steamInventorySerivce.steamInventory();
        System.out.println("接口调用，buff自动上架完成");
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
     * 拉取商品的历史订单
     */
    @RequestMapping("pullOrderHistorys")
    @ResponseBody
    public void pullOrderHistorys() {
        orderHistoryService.pullOrderHistory();
    }




}
