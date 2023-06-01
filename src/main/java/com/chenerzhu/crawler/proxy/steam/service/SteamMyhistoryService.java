package com.chenerzhu.crawler.proxy.steam.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * steam历史记录
 */
@Service
@Slf4j
public class SteamMyhistoryService {


    public void marketMyhistory(int start){
        String url = "https://steamcommunity.com/market/myhistory/render/?query=&count=10&start=" + start;

    }
}
