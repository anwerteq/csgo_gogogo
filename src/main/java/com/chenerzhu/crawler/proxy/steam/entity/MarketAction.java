package com.chenerzhu.crawler.proxy.steam.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "market_actions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 市场操作的链接，例如在游戏中预览物品的链接。
     */
    @Column(name = "action_link")
    private String actionLink;

    /**
     * 市场操作的名称，如“在游戏中检视”。
     */
    @Column(name = "action_name")
    private String actionName;

    /**
     * 物品关联的 CZ75 物品。
     */
    @ManyToOne
    @JoinColumn(name = "cz75_item_id")
    private CZ75Item cz75Item;

}
