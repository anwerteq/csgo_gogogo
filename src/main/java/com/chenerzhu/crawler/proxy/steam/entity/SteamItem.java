package com.chenerzhu.crawler.proxy.steam.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class SteamItem {
    
    @Id
    private String id;  // 物品ID，用于唯一标识物品

    @Column(nullable = false)
    private int currency;  // 货币类型，通常为0，表示基础货币类型

    @Column(nullable = false)
    private int appid;  // 关联的应用ID，通常为游戏的ID

    @Column(nullable = false)
    private String contextid;  // 上下文ID（如库存、市场）

    @Column(nullable = false)
    private String classid;  // 物品类ID，通常用于区分不同类型的物品

    @Column(nullable = false)
    private String instanceid;  // 物品实例ID，区分物品的唯一实例

    @Column(nullable = false)
    private String amount;  // 物品数量（如果是可堆叠物品）

    private int status;  // 物品状态，可能表示是否有可用的购买或交易等状态

    @Column(name = "original_amount")
    private String originalAmount;  // 物品的初始数量，通常用于跟踪物品的变化

    @Column(name = "unowned_id")
    private String unownedId;  // 当前未拥有的物品ID

    @Column(name = "unowned_contextid")
    private String unownedContextid;  // 当前未拥有的物品上下文ID

    private String backgroundColor;  // 物品的背景颜色

    @Column(name = "icon_url")
    private String iconUrl;  // 物品的图标URL

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "steam_item_id")
    private List<Description> descriptions;  // 物品的描述列表，每个描述都有类型和值

    private int tradable;  // 物品是否可交易（1为可交易，0为不可交易）

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "steam_item_id")
    private List<Action> actions;  // 物品的操作列表，例如在Steam中执行某个操作

    private String name;  // 物品的名称

    @Column(name = "name_color")
    private String nameColor;  // 物品名称的颜色（通常是16进制颜色值）

    private String type;  // 物品的类型（例如：StatTrak™ 军规级 手枪）

    @Column(name = "market_name")
    private String marketName;  // 物品在市场中的名称

    @Column(name = "market_hash_name")
    private String marketHashName;  // 物品在市场中的哈希名称

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "steam_item_id")
    private List<Action> marketActions;  // 物品在市场中的操作列表

    private int commodity;  // 物品是否是商品（通常用于表示是否可以在Steam市场交易）

    @Column(name = "market_tradable_restriction")
    private int marketTradableRestriction;  // 物品的市场交易限制（如某些物品不可交易）

    @Column(name = "market_marketable_restriction")
    private int marketMarketableRestriction;  // 物品的市场上架限制

    private int marketable;  // 物品是否可以在市场上架交易

    @Column(name = "app_icon")
    private String appIcon;  // 物品所属应用的图标URL

    private int owner;  // 物品的拥有者状态（0代表没有拥有者，1代表已拥有）

    // Inner class for Description
    @Entity
    @Data

    public static class Description {
        
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;  // 描述的ID，用于唯一标识该描述

        private String type;  // 描述的类型（如 html、text 等）

        private String value;  // 描述的具体值（如物品的外观、特性等）

        private String color;  // 描述的颜色（可选字段）

        private String name;  // 描述的名称（如 exterior_wear、stattrak_type 等）


    }


}
