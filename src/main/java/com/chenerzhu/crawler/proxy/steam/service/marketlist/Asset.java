/**
 * Copyright 2023 json.cn
 */
package com.chenerzhu.crawler.proxy.steam.service.marketlist;

import java.util.List;

/**
 * Auto-generated: 2023-11-04 0:29:52
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class Asset {

    private int currency;
    private int appid;
    private String contextid;
    private String id;
    private String amount;
    private List<Market_actions> market_actions;

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public String getContextid() {
        return contextid;
    }

    public void setContextid(String contextid) {
        this.contextid = contextid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public List<Market_actions> getMarket_actions() {
        return market_actions;
    }

    public void setMarket_actions(List<Market_actions> market_actions) {
        this.market_actions = market_actions;
    }

}
