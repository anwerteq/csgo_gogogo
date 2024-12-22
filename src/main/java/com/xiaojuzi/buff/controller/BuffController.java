package com.xiaojuzi.buff.controller;

import com.xiaojuzi.applicationRunners.BuffApplicationRunner;
import com.xiaojuzi.buff.service.*;
import com.xiaojuzi.config.CookiesConfig;
import com.xiaojuzi.csgo.service.BuffBuyItemService;
import com.xiaojuzi.steam.service.SteamBuyItemService;
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

    @Autowired
    BuffSetMemoService buffSetMemoService;



    /**
     * 自动购买饰品
     */
    @RequestMapping("autoItemGoods")
    @ResponseBody
    public void autoBuyItemGoods() {
        pullItemService.autoBuyItemGoods(false);
    }

    /**
     * 拉取商品市场信息
     */
    @RequestMapping("pullItmeGoods")
    @ResponseBody
    public void pullItem() {
        BuffApplicationRunner.buffUserDataThreadLocal.set(BuffApplicationRunner.buffUserDataList.get(0));
        CookiesConfig.buffCookies.set(BuffApplicationRunner.buffUserDataThreadLocal.get().getCookie());
        pullItemService.pullItmeGoods();
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
        while (true){
            buffBuyItemService.buyHotItem();
            System.out.println("接口调用，购买商品完成");
        }
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
        steamInventorySerivce.steamInventory(1);
        System.out.println("接口调用，buff自动上架完成");
    }



    /**
     * buff自动上架
     */
    @RequestMapping("autoSale")
    @ResponseBody
    public void autoSale() {
        BuffApplicationRunner.buffUserDataThreadLocal.set(BuffApplicationRunner.buffUserDataList.get(0));
        CookiesConfig.buffCookies.set(BuffApplicationRunner.buffUserDataThreadLocal.get().getCookie());
        steamInventorySerivce.autoSale();

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
        BuffApplicationRunner.buffUserDataThreadLocal.set(BuffApplicationRunner.buffUserDataList.get(0));
        CookiesConfig.buffCookies.set(BuffApplicationRunner.buffUserDataThreadLocal.get().getCookie());

        orderHistoryService.pullOrderHistory();
    }



    /**
     * buff商品设置成本价格
     */
    @RequestMapping("remarkChange")
    @ResponseBody
    public void remarkChange() {
        BuffApplicationRunner.buffUserDataThreadLocal.set(BuffApplicationRunner.buffUserDataList.get(0));
        CookiesConfig.buffCookies.set(BuffApplicationRunner.buffUserDataThreadLocal.get().getCookie());
        buffSetMemoService.assetRemarkChange();
    }
}
