package com.chenerzhu.crawler.proxy.steam.dto;

import com.chenerzhu.crawler.proxy.steam.entity.CZ75Item;
import lombok.Data;

import java.util.Map;

@Data
public class MarketListingResponse {
    private boolean success;
    private int pagesize;
    private int total_count;
    private int start;
    private Map<String, Map<String, Map<String, CZ75Item>>> assets;
    private String hovers;
    private String results_html;

}
