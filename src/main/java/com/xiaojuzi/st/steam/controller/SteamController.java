package com.xiaojuzi.st.steam.controller;

import com.xiaojuzi.st.applicationRunners.SteamApplicationRunner;
import com.xiaojuzi.st.config.CookiesConfig;
import com.xiaojuzi.st.steam.entity.Cookeis;
import com.chenerzhu.crawler.proxy.steam.service.*;
import com.xiaojuzi.st.steam.service.*;
import com.xiaojuzi.st.util.steamlogin.SteamUserDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

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
    /**
     * steam库存商品上架市场
     */
    @RequestMapping("grounding")
    public void grounding(){

        groundingService.productListingOperation();
        List<Cookeis> cookeisList = CookiesConfig.cookeisList;
        for (Cookeis cookeis : cookeisList) {
            CookiesConfig.steamCookies.set(cookeis.getSteam_cookie());
            try {
                groundingService.productListingOperation();
            }catch (Exception e){
                log.error("账号：{}，拉取steam市场信息异常：{}",cookeis.getNumber(),e);
            }
            CookiesConfig.steamCookies.set("");
        }
    }


    /**
     * 下架商品
     */
    @RequestMapping("unlistingBlock")
    @ResponseBody
    public void unlistingBlock(@RequestParam(value = "sum", required = false, defaultValue = "1") int sum) {
        removelistingService.unlistings(sum);
        List<Cookeis> cookeisList = CookiesConfig.cookeisList;
        for (Cookeis cookeis : cookeisList) {
            CookiesConfig.steamCookies.set(cookeis.getSteam_cookie());

            CookiesConfig.steamCookies.set("");
        }
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
        Optional<SteamUserDate> first = SteamApplicationRunner.steamUserDates.stream().filter(o -> name.toLowerCase().equals(o.getAccount_name().toLowerCase())).findFirst();
        if (!first.isPresent()) {
            throw new RuntimeException("账号："+name+"不存");
        }
        SteamUserDate steamUserDate1 = (SteamUserDate) first.get();
        SteamApplicationRunner.steamUserDateTL.set(steamUserDate1);
        steamMyhistoryService.marketMyhistorys();
    }






}
