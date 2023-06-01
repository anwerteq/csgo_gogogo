package com.chenerzhu.crawler.proxy.steam.entity;

import lombok.Data;

@Data
public class SteamMyhistoryRoot{

    private boolean success;
    private int pagesize;
    private int total_count;
    private int start;
    private Object assets;
    private String hovers;
    private String results_html;
}
