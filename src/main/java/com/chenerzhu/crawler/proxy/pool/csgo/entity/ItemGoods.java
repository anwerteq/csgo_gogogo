/**
 * Copyright 2023 bejson.com
 */
package com.chenerzhu.crawler.proxy.pool.csgo.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * Auto-generated: 2023-05-14 0:18:12
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@ToString
@Entity
@Table(name = "item_goods")
public class ItemGoods {

    private static final long serialVersionUID = 1L;
    @Id
    private long id;

    @Column(name = "appid")
    private int appid;
    @Column(name = "bookmarked")
    private boolean bookmarked;
    @Column(name = "buy_max_price")
    private String buy_max_price;
    @Column(name = "buy_num")
    private int buy_num;
    @Column(name = "can_bargain")
    private boolean can_bargain;
    @Column(name = "can_search_by_tournament")
    private boolean can_search_by_tournament;
    @Column(name = "description")
    private String description;
    @Column(name = "game")
    private String game;

    @Transient
    private Goods_info goods_info;
    @Column(name = "has_buff_price_history")
    private boolean has_buff_price_history;
    @Column(name = "market_hash_name")
    private String market_hash_name;
    @Column(name = "market_min_price")
    private String market_min_price;
    @Column(name = "name")
    private String name;
    @Column(name = "quick_price")
    private String quick_price;
    @Column(name = "sell_min_price")
    private String sell_min_price;
    @Column(name = "sell_num")
    private long sell_num;
    @Column(name = "sell_reference_price")
    private String sell_reference_price;
    @Column(name = "short_name")
    private String short_name;
    @Column(name = "steam_market_url")
    private String steam_market_url;
    @Column(name = "transacted_num")
    private int transacted_num;

}
