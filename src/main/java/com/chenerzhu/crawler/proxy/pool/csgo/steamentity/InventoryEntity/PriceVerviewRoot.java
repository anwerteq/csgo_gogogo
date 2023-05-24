/**
 * Copyright 2023 json.cn
 */
package com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity;

import lombok.Data;

/**
 * Auto-generated: 2023-05-22 17:34:12
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class PriceVerviewRoot {

    private boolean success;
    /**
     * 商品类目
     */
    private String classid;

    /**
     * 当前最低售价
     */
    private String lowest_price;
    /**
     * 商品售卖数量
     */
    private String volume;
    /**
     * 价格中位数
     */
    private String median_price;

    /**
     * 游戏id
     */
    private int appid;

}