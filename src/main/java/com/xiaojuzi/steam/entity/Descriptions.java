/**
 * Copyright 2023 json.cn
 */
package com.xiaojuzi.steam.entity;

import com.xiaojuzi.csgo.steamentity.InventoryEntity.Actions;
import com.xiaojuzi.csgo.steamentity.InventoryEntity.Market_actions;
import com.xiaojuzi.csgo.steamentity.InventoryEntity.Owner_descriptions;
import com.xiaojuzi.csgo.steamentity.InventoryEntity.Tags;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;

/**
 * steam仓库饰品信息
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
@ToString
@Entity
@Table(name = "steam_descriptions")
public class Descriptions {

    @Id
    private String cdkey_id;
   @Column
    private String assetid;
   @Column
   private int appid;
   @Column
   private String classid;
   @Column
   private String instanceid;
   @Column
   private int currency;
   @Column
   private String background_color;
   @Column
   private String icon_url;
   @Column
   private String icon_url_large;
   @Transient
   private List<Descriptions> descriptions;
   @Column
   private int tradable;
   @Transient
   private List<Actions> actions;
   @Transient
   private List<Owner_descriptions> owner_descriptions;
   @Column
   private String name;
   @Column
   private String name_color;
   @Column
   private String type;
   @Column
   private String market_name;
   @Column
   private String market_hash_name;
   @Transient
   private List<Market_actions> market_actions;
   @Column
   private int commodity;
   @Column
   private int market_tradable_restriction;
   @Column
   private int marketable;
   @Transient
   private List<Tags> tags;
   @Column
   private Integer amount = 1;

    @Column
    private String number_name;

    @Column
    private String steamId;

    /**
     * 仓库逻辑id
     */
    @Column
    private String steamInventoryMarkId;
    /**
     * buff最低价值
     */
    @Column
    private Double buff_min_price;

    /**
     * steam最低价值
     */
    @Column
    private Double steam_price;

    /**
     * 购买价格
     */
    @Column
    private Double buy_price;

    /**
     * 购买平台
     */
    @Column
    private String  buy_type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime create_date;


    @Transient
    public void refreashCdkey_id() {
        StringJoiner sj = new StringJoiner("-");
        sj.add(assetid);
        sj.add(classid);
        sj.add(instanceid);
        setCdkey_id(sj.toString());
    }


    /**
     * 刷新仓库id
     */
    public void refreshSteamInventoryMarkId(){
        setSteamInventoryMarkId(classid +"-"+ instanceid);

    }
}
