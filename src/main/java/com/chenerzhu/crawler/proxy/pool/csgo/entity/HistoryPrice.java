package com.chenerzhu.crawler.proxy.pool.csgo.entity;

import lombok.Data;

import java.util.List;


@Data
public class HistoryPrice {


    private String currency;
    private String currency_symbol;
    private int days;
    private List<List<String>> price_history;
    private String price_type;
    private String steam_price_currency;

}
