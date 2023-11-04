package com.chenerzhu.crawler.proxy.steam.service.marketlist;

import lombok.Data;

/**
 * steam低磨损封装的数据
 */
@Data
public class SteamLossItemDetail {
    String listId;
    String priceDollar;
    String url;
    String painwear;
    String name;
    String hashName;

    public String getLinkKey() {
        String linkKey = url.split("A")[1].split("D")[0];
        return linkKey;
    }
}
