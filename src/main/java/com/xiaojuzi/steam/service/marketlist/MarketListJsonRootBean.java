/**
 * Copyright 2023 json.cn
 */
package com.xiaojuzi.steam.service.marketlist;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2023-11-04 0:29:52
 *
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class MarketListJsonRootBean {

    private boolean success;
    private int start;
    private int pagesize;
    private int total_count;
    private String results_html;
    private JSONObject listinginfo;
    private JSONObject assets;
    private List<String> currency;
    private String hovers;
//    private App_data app_data;
}
