package com.chenerzhu.crawler.proxy.pool.csgo.BuffBuyItemEntity;

import lombok.Data;

@Data
public class Asset_info
{
    private String action_link;

    private int appid;

    private String assetid;

    private String classid;

    private int contextid;

    private int goods_id;

    private boolean has_tradable_cooldown;

    private String id;

    private Object info;

    private String instanceid;

    private String paintwear;

    private String tradable_cooldown_text;

    private int tradable_unfrozen_time;

}
