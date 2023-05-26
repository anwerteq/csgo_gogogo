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
@Table(name = "goods_info")
public class Goods_info {

    private String icon_url;
    @Transient
    private Info info;
    @Id
    private long item_id;
    @Column(name = "original_icon_url",length = 600)
    private String original_icon_url;
    private String steam_price;
    private String steam_price_cny;
    public void setIcon_url(String icon_url) {
         this.icon_url = icon_url;
     }
     public String getIcon_url() {
         return icon_url;
     }

    public void setInfo(Info info) {
         this.info = info;
     }
     public Info getInfo() {
         return info;
     }

}
