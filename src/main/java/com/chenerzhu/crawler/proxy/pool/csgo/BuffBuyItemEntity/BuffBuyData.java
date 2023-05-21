package com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity;

import com.chenerzhu.crawler.proxy.pool.csgo.entity.Goods_info;
import lombok.Data;

import java.util.List;
@Data
public class BuffBuyData
{
    private String fop_str;

    private Goods_info goods_infos;

    private Object has_market_stores;

    private List<BuffBuyItems> items;

    private int page_num;

    private int page_size;

    private Object preview_screenshots;

    private boolean show_game_cms_icon;

    private boolean show_pay_method_icon;

    private String sort_by;

    private String src_url_background;

    private int total_count;

    private int total_page;

    private Object user_infos;

}
