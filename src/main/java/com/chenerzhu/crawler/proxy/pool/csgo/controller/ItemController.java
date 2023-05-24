package com.chenerzhu.crawler.proxy.pool.csgo.controller;

import com.chenerzhu.crawler.proxy.pool.buff.service.PullHistoryService;
import com.chenerzhu.crawler.proxy.pool.buff.service.PullItemService;
import com.chenerzhu.crawler.proxy.pool.controller.BaseController;
import com.chenerzhu.crawler.proxy.pool.csgo.service.BuffBuyItemService;
import com.chenerzhu.crawler.proxy.pool.csgo.service.ItemGoodsService;
import com.chenerzhu.crawler.proxy.pool.csgo.service.SteamItemService;
import com.chenerzhu.crawler.proxy.pool.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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

    @Autowired
    SteamItemService steamItemService;

    @Autowired
    BuffBuyItemService buffBuyItemService;

    @Autowired
    ProxyUtils proxyUtils;

    @Autowired
    PullItemService pullItemService;

    @Autowired
    PullHistoryService pullHistoryService;

    /**
     * 拉取商品简要信息
     */
    @RequestMapping("pullItmeGoods")
    @ResponseBody
    public void pullItem() {
        pullItemService.pullItmeGoods();
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
     * 拉取商品的历史记录
     */
    @RequestMapping("pullSteamItems")
    @ResponseBody
    public void pullSteamItems() {
        steamItemService.pullItems();
    }



    /**
     * 购买buff中的商品
     */
    @RequestMapping("buffBuyItems")
    @ResponseBody
    public void buffBuyItems() {
        buffBuyItemService.buffSellOrder("903822",1);
        System.out.println("t推出接口调用");
    }



    /**
     * 购买buff中的商品
     */
    @RequestMapping("test")
    @ResponseBody
    public String  test() {
//        buffBuyItemService.saleItem("1","1");/
        return  "";

    }


}
