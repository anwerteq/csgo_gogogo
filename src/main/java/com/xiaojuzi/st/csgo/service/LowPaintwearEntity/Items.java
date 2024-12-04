/**
  * Copyright 2023 ab173.com 
  */
package com.xiaojuzi.st.csgo.service.LowPaintwearEntity;

import lombok.Data;

/**
 * Auto-generated: 2023-10-28 0:0:22
 *
 * @author ab173.com (info@ab173.com)
 * @website http://www.ab173.com/json/
 */

@Data
public class Items {

    private boolean allow_bargain;
    private int appid;
    private Asset_info asset_info;
    private String background_image_url;
    private boolean can_bargain;
    private boolean can_use_inspect_trn_url;
    private String cannot_bargain_reason;
    private Object coupon_infos;
    private long created_at;
    private String description;
    private int featured;
    private String fee;
    private String game;
    private long goods_id;
    private String id;
    private String img_src;
    private String income;
    private String lowest_bargain_price;
    private int mode;
    private String price;
    private String recent_average_duration;
    private String recent_deliver_rate;
    private int state;
    private String tradable_cooldown;
    private long updated_at;
    private String user_id;
    public void setAllow_bargain(boolean allow_bargain) {
         this.allow_bargain = allow_bargain;
     }
     public boolean getAllow_bargain() {
         return allow_bargain;
     }

    public void setAppid(int appid) {
         this.appid = appid;
     }
     public int getAppid() {
         return appid;
     }

    public void setAsset_info(Asset_info asset_info) {
         this.asset_info = asset_info;
     }
     public Asset_info getAsset_info() {
         return asset_info;
     }

    public void setBackground_image_url(String background_image_url) {
         this.background_image_url = background_image_url;
     }
     public String getBackground_image_url() {
         return background_image_url;
     }

    public void setCan_bargain(boolean can_bargain) {
         this.can_bargain = can_bargain;
     }
     public boolean getCan_bargain() {
         return can_bargain;
     }

    public void setCan_use_inspect_trn_url(boolean can_use_inspect_trn_url) {
         this.can_use_inspect_trn_url = can_use_inspect_trn_url;
     }
     public boolean getCan_use_inspect_trn_url() {
         return can_use_inspect_trn_url;
     }

    public void setCannot_bargain_reason(String cannot_bargain_reason) {
         this.cannot_bargain_reason = cannot_bargain_reason;
     }
     public String getCannot_bargain_reason() {
         return cannot_bargain_reason;
     }


    public void setCreated_at(long created_at) {
         this.created_at = created_at;
     }
     public long getCreated_at() {
         return created_at;
     }

    public void setDescription(String description) {
         this.description = description;
     }
     public String getDescription() {
         return description;
     }

    public void setFeatured(int featured) {
         this.featured = featured;
     }
     public int getFeatured() {
         return featured;
     }

    public void setFee(String fee) {
         this.fee = fee;
     }
     public String getFee() {
         return fee;
     }

    public void setGame(String game) {
         this.game = game;
     }
     public String getGame() {
         return game;
     }

    public void setGoods_id(long goods_id) {
         this.goods_id = goods_id;
     }
     public long getGoods_id() {
         return goods_id;
     }

    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

    public void setImg_src(String img_src) {
         this.img_src = img_src;
     }
     public String getImg_src() {
         return img_src;
     }

    public void setIncome(String income) {
         this.income = income;
     }
     public String getIncome() {
         return income;
     }

    public void setLowest_bargain_price(String lowest_bargain_price) {
         this.lowest_bargain_price = lowest_bargain_price;
     }
     public String getLowest_bargain_price() {
         return lowest_bargain_price;
     }

    public void setMode(int mode) {
         this.mode = mode;
     }
     public int getMode() {
         return mode;
     }

    public void setPrice(String price) {
         this.price = price;
     }
     public String getPrice() {
         return price;
     }

    public void setRecent_average_duration(String recent_average_duration) {
         this.recent_average_duration = recent_average_duration;
     }
     public String getRecent_average_duration() {
         return recent_average_duration;
     }

    public void setRecent_deliver_rate(String recent_deliver_rate) {
         this.recent_deliver_rate = recent_deliver_rate;
     }
     public String getRecent_deliver_rate() {
         return recent_deliver_rate;
     }

    public void setState(int state) {
         this.state = state;
     }
     public int getState() {
         return state;
     }

    public void setTradable_cooldown(String tradable_cooldown) {
         this.tradable_cooldown = tradable_cooldown;
     }
     public String getTradable_cooldown() {
         return tradable_cooldown;
     }

    public void setUpdated_at(long updated_at) {
         this.updated_at = updated_at;
     }
     public long getUpdated_at() {
         return updated_at;
     }

    public void setUser_id(String user_id) {
         this.user_id = user_id;
     }
     public String getUser_id() {
         return user_id;
     }

}