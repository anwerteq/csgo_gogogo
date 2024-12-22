package com.xiaojuzi.steam.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "csgo_items",indexes = {
@Index(name = "idx_steam_inventory_mark_id", columnList = "steamInventoryMarkId") // 添加索引
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CZ75Item {

    @Id
    private Long id;

    /**
     * 物品的货币类型，例如：0 表示没有货币（默认）。
     */
    @Column(name = "currency")
    private Integer currency;

    /**
     * 物品所属的应用程序 ID。对 `Counter-Strike: Global Offensive` 来说，通常为 730。
     */
    @Column(name = "appid")
    private Integer appid;

    /**
     * 物品的上下文 ID。每个物品在同一应用中的上下文 ID 可以不同。
     */
    @Column(name = "contextid")
    private String contextid;

    /**
     * 物品的唯一 ID。
     */
    @Column(name = "item_id")
    private String itemId;

    /**
     * 物品的类 ID，通常与物品的类型或模型相关。
     */
    @Column(name = "classid")
    private String classId;

    /**
     * 物品的实例 ID，用于区分同一类型物品的不同实例。
     */
    @Column(name = "instanceid")
    private String instanceId;

    /**
     * 物品数量，通常为 1。如果物品数量为 0，表示物品不可用。
     */
    @Column(name = "amount")
    private Integer amount;

    /**
     * 物品的状态代码，通常表示物品的状态（如新、已售出、无效等）。
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 物品的原始数量，通常为 1。
     */
    @Column(name = "original_amount")
    private Integer originalAmount;

    /**
     * 未拥有物品的 ID（如果物品没有所有者，值为物品 ID）。
     */
    @Column(name = "unowned_id")
    private String unownedId;

    /**
     * 未拥有物品的上下文 ID。
     */
    @Column(name = "unowned_contextid")
    private String unownedContextId;

    /**
     * 物品的背景颜色，可以为空。
     */
    @Column(name = "background_color")
    private String backgroundColor;

    /**
     * 物品图标的 URL，通常为物品的缩略图。
     */
    @Column(name = "icon_url")
    private String iconUrl;

    /**
     * 物品是否可交易，1 表示可交易，0 表示不可交易。
     */
    @Column(name = "tradable")
    private Integer tradable;

    /**
     * 物品在市场中的名称。
     */
    @Column(name = "market_name")
    private String marketName;

    /**
     * 物品在市场中的哈希名称。
     */
    @Column(name = "market_hash_name")
    private String marketHashName;

    /**
     * 物品是否为商品，1 表示是，0 表示否。
     */
    @Column(name = "commodity")
    private Integer commodity;

    /**
     * 物品的市场交易限制，例如：7 表示可以在市场中进行交易。
     */
    @Column(name = "market_tradable_restriction")
    private Integer marketTradableRestriction;

    /**
     * 物品的市场买卖限制，例如：7 表示可以进行买卖。
     */
    @Column(name = "market_marketable_restriction")
    private Integer marketMarkableRestriction;

    /**
     * 物品是否可以在市场中买卖，1 表示可以，0 表示不可。
     */
    @Column(name = "marketable")
    private Integer marketable;

    /**
     * 物品应用的图标 URL，通常显示在库存页面或市场页面上。
     */
    @Column(name = "app_icon")
    private String appIcon;

    /**
     * 物品的所有者 ID，0 表示物品没有所有者。
     */
    @Column(name = "owner")
    private Integer owner;

    /**
     * 物品的描述列表，存储多个描述信息。
     */
    @ElementCollection
    @CollectionTable(name = "cz75_descriptions", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "description", columnDefinition = "TEXT")
    private List<String> descriptions;

    /**
     * 物品关联的印花列表。
     */
    @OneToMany(mappedBy = "cz75Item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Sticker> stickers;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private List<Action> actions; // 动作列表

    @Column
    private Double usd;

    /**
     * 上架日期
     */
    @Column
    private String listingDate;

    /**
     * 交易日期
     */
    @Column
    private String tradingDate;

    /**
     * memo
     */
    @Column
    private String memo;

    /**
     * 仓库逻辑id
     */
    @Column
    private String steamInventoryMarkId;

    /**
     * 交易类型（买，或者 卖）
     */
    @Column
    private String theTypeOfTransaction;

    /**
     * steamId
     */
    @Column
    private String steamId;


    /**
     * 刷新仓库id
     */
    public void refreshSteamInventoryMarkId(){
        setSteamInventoryMarkId(classId +"-"+ instanceId);

    }

}
