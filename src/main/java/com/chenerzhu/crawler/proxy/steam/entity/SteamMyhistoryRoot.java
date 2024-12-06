package com.chenerzhu.crawler.proxy.steam.entity;

import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Assets;
import lombok.Data;

import java.util.List;

@Data
public class SteamMyhistoryRoot{

    private boolean success;
    private int pagesize;
    private int total_count;
    private int start;
    private List<Assets> assets;
    private String hovers;
    private String results_html;
}
