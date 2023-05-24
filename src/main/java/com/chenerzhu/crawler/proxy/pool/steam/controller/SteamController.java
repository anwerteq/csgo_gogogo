package com.chenerzhu.crawler.proxy.pool.steam.controller;

import com.chenerzhu.crawler.proxy.pool.steam.service.GroundingService;
import com.chenerzhu.crawler.proxy.pool.steam.service.SteamItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 上架商品接口
     */
    @RequestMapping("grounding")
    public void grounding(){
        groundingService.productListingOperation();
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
