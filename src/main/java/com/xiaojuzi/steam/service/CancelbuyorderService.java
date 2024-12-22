package com.xiaojuzi.steam.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CancelbuyorderService {

    public void cancelBuyOrder() {
        String url = "https://steamcommunity.com/market/cancelbuyorder/";
    }
}
