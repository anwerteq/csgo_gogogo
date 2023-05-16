package com.chenerzhu.crawler.proxy.pool.csgo.controller;

import com.chenerzhu.crawler.proxy.pool.controller.BaseController;
import com.chenerzhu.crawler.proxy.pool.csgo.service.ItemGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    /**
     * 拉取商品简要信息
     */
    @RequestMapping("pullItmeGoods")
    @ResponseBody
    public void pullItem() {
        itemGoodsService.pullItmeGoods();

    }


    /**
     * 拉取商品的历史记录
     */
    @RequestMapping("pullHistoryPrice")
    @ResponseBody
    public void pullHistoryPrice() {
        Boolean flag = true;
        while (flag) {
            try {
                itemGoodsService.pullHistoryPrice();
                flag=false;
            }catch (Exception e){
                try {
                    Thread.sleep(5 * 1000 * 60);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

        }

    }

}
