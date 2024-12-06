package com.chenerzhu.crawler.proxy.steam.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "stickers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 印花名称，通常是印花的标识符或名称。
     */
    @Column(name = "sticker_name")
    private String stickerName;

    /**
     * 印花图标的 URL，用于显示印花的图像。
     */
    @Column(name = "sticker_url")
    private String stickerUrl;

    /**
     * 印花的标题，描述印花的特性或含义。
     */
    @Column(name = "sticker_title")
    private String stickerTitle;

    /**
     * 与印花关联的 CZ75 物品。
     */
    @ManyToOne
    @JoinColumn(name = "cz75_item_id")
    private CZ75Item cz75Item;

}
