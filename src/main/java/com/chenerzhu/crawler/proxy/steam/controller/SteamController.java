package com.chenerzhu.crawler.proxy.steam.controller;

import com.chenerzhu.crawler.proxy.steam.service.GroundingService;
import com.chenerzhu.crawler.proxy.steam.service.RemovelistingService;
import com.chenerzhu.crawler.proxy.steam.service.SteamItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    RemovelistingService removelistingService;

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



    /**
     * 下架商品
     */
    @RequestMapping("unlistingBlock")
    @ResponseBody
    public void unlistingBlock(@RequestParam(value = "sum", required = false, defaultValue = "1") int sum) {

        removelistingService.unlistings(sum);
    }

}
