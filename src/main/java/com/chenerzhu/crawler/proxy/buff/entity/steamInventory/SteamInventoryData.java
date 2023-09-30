/**
 * Copyright 2023 json.cn
 */
package com.chenerzhu.crawler.proxy.buff.entity.steamInventory;
import com.chenerzhu.crawler.proxy.buff.entity.steamtradeentity.Goods_infos;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.Items;

import java.util.List;

@lombok.Data
public class SteamInventoryData {

    private String brief_info;
    private String currency;
    private String currency_symbol;
    private boolean depositable;
    private String fop_str;
    private Goods_infos goods_infos;
    private String inventory_price;
    private List<Items> items;
    private boolean manual_plus_sellable;
    private boolean manual_sellable;
    private int page_num;
    private int page_size;
    private Object preview_screenshots;
    private Object progress_desc;
    private String src_url_background;
    private Object state_desc;
    private String total_amount;
    private String total_amount_usd;
    private int total_count;
    private int total_page;
}
