package com.chenerzhu.crawler.proxy.steam.controller;

import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.entity.Cookeis;
import com.chenerzhu.crawler.proxy.steam.service.GroundingService;
import com.chenerzhu.crawler.proxy.steam.service.ListingsService;
import com.chenerzhu.crawler.proxy.steam.service.RemovelistingService;
import com.chenerzhu.crawler.proxy.steam.service.SteamItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 操作steam的控制层
 */
@RestController
@RequestMapping("/steam")
public class SteamController {

    @Autowired
    GroundingService groundingService;


    @Autowired
    SteamItemService steamItemService;

    @Autowired
    RemovelistingService removelistingService;

    @Autowired
    ListingsService listingsService;

    /**
     * steam库存商品上架市场
     */
    @RequestMapping("grounding")
    public void grounding(){
        List<Cookeis> cookeisList = CookiesConfig.cookeisList;
        for (Cookeis cookeis : cookeisList) {
            CookiesConfig.steamCookies.set(cookeis.getSteam_cookie());
            groundingService.productListingOperation();
            CookiesConfig.steamCookies.set("");
        }
    }


    /**
     * 下架商品
     */
    @RequestMapping("unlistingBlock")
    @ResponseBody
    public void unlistingBlock(@RequestParam(value = "sum", required = false, defaultValue = "1") int sum) {
        List<Cookeis> cookeisList = CookiesConfig.cookeisList;
        for (Cookeis cookeis : cookeisList) {
            CookiesConfig.steamCookies.set(cookeis.getSteam_cookie());
            removelistingService.unlistings(sum);
            CookiesConfig.steamCookies.set("");
        }
    }

    /**
     * 拉取steam市场信息
     */
    @RequestMapping("pullSteamItem")
    @ResponseBody
    public void pullSteamItem() {
        List<Cookeis> cookeisList = CookiesConfig.cookeisList;
        for (Cookeis cookeis : cookeisList) {
            CookiesConfig.steamCookies.set(cookeis.getSteam_cookie());
            listingsService.pullItems();
            CookiesConfig.steamCookies.set("");
        }
    }


    /**
     * 拉取商品的历史记录
     */
    @RequestMapping("pullSteamItems")
    @ResponseBody
    public void pullSteamItems() {
        steamItemService.pullItems();
    }






}
