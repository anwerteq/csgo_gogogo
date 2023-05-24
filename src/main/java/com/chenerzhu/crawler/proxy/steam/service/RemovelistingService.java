package com.chenerzhu.crawler.proxy.steam.service;

import org.springframework.stereotype.Service;

/**
 * steam商品下架
 */
@Service
public class RemovelistingService {


    public void getMylistings(){
        String url = "https://steamcommunity.com/market/mylistings/render/?query=&start=1&count=100";
    }
    public void removeList(){

    }
}
