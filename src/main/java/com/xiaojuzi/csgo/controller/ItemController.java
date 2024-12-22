package com.xiaojuzi.csgo.controller;

import com.xiaojuzi.buff.service.PullHistoryService;
import com.xiaojuzi.buff.service.PullItemService;
import com.xiaojuzi.csgo.service.BuffBuyItemService;
import com.xiaojuzi.csgo.service.ItemGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @create 2018-08-29 19:51
 **/
@Slf4j
@Controller
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemGoodsService itemGoodsService;


    @Autowired
    BuffBuyItemService buffBuyItemService;



    @Autowired
    PullItemService pullItemService;

    @Autowired
    PullHistoryService pullHistoryService;


    /**
     * 拉取商品的历史交易价格
     */
    @RequestMapping("pullHistoryPrice")
    @ResponseBody
    public void pullHistoryPrice() {
        pullHistoryService.pullHistoryPrice();
    }




}
