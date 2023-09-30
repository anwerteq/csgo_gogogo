
package com.chenerzhu.crawler.proxy.csgo.steamentity;

import lombok.Data;

import java.util.List;

@Data
public class SteamSearchdata {
    private String query;

    private boolean search_descriptions;

    private int total_count;

    private int pagesize;

    private String prefix;

    private String class_prefix;

    private List<SteamItem> results;
}
