package com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity;

import lombok.Data;

import java.util.List;

@Data
public class BuyOrderHistoryData {
    private Object goods_infos;

    private List<Items> items;

    private int page_num;

    private int page_size;

    private int total_count;

    private int total_page;

    private Object user_infos;

}
