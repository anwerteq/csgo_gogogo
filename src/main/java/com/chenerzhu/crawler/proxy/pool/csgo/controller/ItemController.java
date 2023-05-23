package com.chenerzhu.crawler.proxy.pool.csgo.controller;

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


    /**
     * 拉取商品的历史记录
     */
    @RequestMapping("pullSteamItems")
    @ResponseBody
    public void pullSteamItems() {
        steamItemService.pullItems();
    }


    /**
     * 拉取商品的历史记录
     */
    @RequestMapping("selectHistory1")
    @ResponseBody
    public Page selectHistory1() {
        return   itemGoodsService.selectHistory1();

    }


    /**
     * 购买buff中的商品
     */
    @RequestMapping("buffBuyItems")
    @ResponseBody
    public void buffBuyItems() {
        buffBuyItemService.buffSellOrder("903822",1);

    }



    /**
     * 购买buff中的商品
     */
    @RequestMapping("test")
    @ResponseBody
    public String  test() {
//        buffBuyItemService.saleItem("1","1");/
        buffBuyItemService.getSteamInventory();
        return  "";

    }

    public static String getContent(String url, Map<String, String> mapdata) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httpPost = new HttpPost(url);
        try {
            // 设置提交方式
            for (Map.Entry<String, String> entry : BuffBuyItemService.getSaleHeader().entrySet()) {
                httpPost.addHeader(entry.getKey(),entry.getValue());
            }
            httpPost.addHeader("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
            // 添加参数
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            if (mapdata.size() != 0) {
                // 将mapdata中的key存在set集合中，通过迭代器取出所有的key，再获取每一个键对应的值
                Set keySet = mapdata.keySet();
                Iterator it = keySet.iterator();
                while (it.hasNext()) {
                    String k =  it.next().toString();// key
                    String v = mapdata.get(k);// value
                    nameValuePairs.add(new BasicNameValuePair(k, v));
                }
            }
            httpPost.setEntity( new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            // 执行http请求
            response = httpClient.execute(httpPost);
            // 获得http响应体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 响应的结果
                String content = EntityUtils.toString(entity, "UTF-8");
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "获取数据错误";
    }

    public static void main(String[] args) {
        Map<String, String> paramerMap = new HashMap<>();
        paramerMap.put("sessionid", "6ae449625751c147d2e777d9");
        paramerMap.put("appid", "730");
        paramerMap.put("contextid", "2");
        paramerMap.put("assetid", "30483593352");
        paramerMap.put("amount", "1");
        paramerMap.put("price", "25");
        getContent("https://steamcommunity.com/market/sellitem?",paramerMap);
    }

}
