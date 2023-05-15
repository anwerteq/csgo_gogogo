package com.chenerzhu.crawler.proxy.pool.csgo.controller;

import com.chenerzhu.crawler.proxy.pool.controller.BaseController;
import com.chenerzhu.crawler.proxy.pool.csgo.service.ItemGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author chenerzhu
 * @create 2018-08-29 19:51
 **/
@Slf4j
@Controller
@RequestMapping("item")
public class ItemController extends BaseController {

    @Autowired
    private ItemGoodsService itemGoodsService;

    @RequestMapping("pullItme")
    public void pullItem(){
        itemGoodsService.pullItem();

    }

}
