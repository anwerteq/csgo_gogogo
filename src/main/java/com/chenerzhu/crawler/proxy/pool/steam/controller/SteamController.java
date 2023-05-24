package com.chenerzhu.crawler.proxy.pool.steam.controller;

import com.chenerzhu.crawler.proxy.pool.steam.service.GroundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作steam的控制层
 */
@RestController
@RequestMapping("/steam")
public class SteamController {

    @Autowired
    GroundingService groundingService;

    /**
     * 上架商品接口
     */
    @RequestMapping("grounding")
    public void grounding(){
        groundingService.productListingOperation();
    }
}
