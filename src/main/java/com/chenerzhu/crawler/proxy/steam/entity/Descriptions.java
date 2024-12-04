/**
 * Copyright 2023 json.cn
 */
package com.chenerzhu.crawler.proxy.steam.entity;

import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Actions;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Market_actions;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Owner_descriptions;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Tags;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import javax.persistence.Column;
import java.util.List;
import java.util.StringJoiner;

/**
 * Auto-generated: 2023-05-22 14:41:55
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

    @Transient
    public String assetidClassidInstanceid() {
        StringJoiner sj = new StringJoiner("-");
        sj.add(assetid);
        sj.add(classid);
        sj.add(instanceid);
        return sj.toString();
    }
}
