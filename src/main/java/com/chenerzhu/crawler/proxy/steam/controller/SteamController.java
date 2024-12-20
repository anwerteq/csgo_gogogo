package com.chenerzhu.crawler.proxy.steam.controller;

import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.entity.Cookeis;
import com.chenerzhu.crawler.proxy.steam.service.*;
import com.chenerzhu.crawler.proxy.util.SteamTheadeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 操作steam的控制层
 */
@RestController
@RequestMapping("/steam")
@Slf4j
public class SteamController {

    @Autowired
    GroundingService groundingService;


    @Autowired
    SteamItemService steamItemService;

    @Autowired
    RemovelistingService removelistingService;

    @Autowired
    ListingsService listingsService;

    @Autowired
    SteamMyhistoryService steamMyhistoryService;

    @Autowired
    SteamInventoryService steamInventoryService;
    /**
     * steam库存商品上架市场
     */
    @RequestMapping("grounding")
    public void grounding(String name){
        SteamTheadeUtil.setThreadSteamUserDate(name);
        groundingService.productListingOperation();

    }

    /**
     * 刷新steam库存信息
     */
    @RequestMapping("refreshSteamInventory")
    public void refreshSteamInventory(String name){
        SteamTheadeUtil.setThreadSteamUserDate(name);
        steamInventoryService.refreshSteamInventory();
    }



    /**
     * 下架商品
     */
    @RequestMapping("unlistingBlock")
    @ResponseBody
    public void unlistingBlock(@RequestParam(value = "sum", required = false, defaultValue = "1") int sum,String name) {
        CompletableFuture.runAsync(()-> {
            SteamTheadeUtil.setThreadSteamUserDate(name);
            removelistingService.unlistings(sum);
        });

    }

    /**
     * 拉取steam市场信息
     */
    @RequestMapping("pullSteamItem")
    @ResponseBody
    public void pullSteamItem() {
        listingsService.pullItems();
    }


    /**
     * 拉取商品的历史记录
     */
    @RequestMapping("marketMyhistorys")
    @ResponseBody
    public void pullSteamItems(String name) {
        SteamTheadeUtil.setThreadSteamUserDate(name);
        steamMyhistoryService.marketMyhistorys();
    }






}
