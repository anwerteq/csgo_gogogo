package com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity;

import lombok.Data;

@Data
public class BuyOrderHistoryRoot {
    private String code;

    private BuyOrderHistoryData data;

    private String msg;

}
