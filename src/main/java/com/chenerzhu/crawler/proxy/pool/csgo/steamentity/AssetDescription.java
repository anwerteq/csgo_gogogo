package com.chenerzhu.crawler.proxy.pool.csgo.steamentity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Data
@ToString
@Entity
@Table(name = "steam_description")
public class AssetDescription {
    @Id
    private int appid;

    private String classid;

    private String instanceid;

    private int currency;

    private String background_color;

    private String icon_url;

    private String icon_url_large;


    @Transient
    private List<Descriptions> descriptions;

    private int tradable;

    private String name;

    private String name_color;

    private String type;

    private String market_name;

    private String market_hash_name;

    private int commodity;

    private int market_tradable_restriction;

    private int marketable;

    private String market_buy_country_restriction;

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public int getAppid() {
        return this.appid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getClassid() {
        return this.classid;
    }

    public void setInstanceid(String instanceid) {
        this.instanceid = instanceid;
    }

    public String getInstanceid() {
        return this.instanceid;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public int getCurrency() {
        return this.currency;
    }

    public void setBackground_color(String background_color) {
        this.background_color = background_color;
    }

    public String getBackground_color() {
        return this.background_color;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getIcon_url() {
        return this.icon_url;
    }

    public void setIcon_url_large(String icon_url_large) {
        this.icon_url_large = icon_url_large;
    }

    public String getIcon_url_large() {
        return this.icon_url_large;
    }

    public void setDescriptions(List<Descriptions> descriptions) {
        this.descriptions = descriptions;
    }

    public List<Descriptions> getDescriptions() {
        return this.descriptions;
    }

    public void setTradable(int tradable) {
        this.tradable = tradable;
    }

    public int getTradable() {
        return this.tradable;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName_color(String name_color) {
        this.name_color = name_color;
    }

    public String getName_color() {
        return this.name_color;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setMarket_name(String market_name) {
        this.market_name = market_name;
    }

    public String getMarket_name() {
        return this.market_name;
    }

    public void setMarket_hash_name(String market_hash_name) {
        this.market_hash_name = market_hash_name;
    }

    public String getMarket_hash_name() {
        return this.market_hash_name;
    }

    public void setCommodity(int commodity) {
        this.commodity = commodity;
    }

    public int getCommodity() {
        return this.commodity;
    }

    public void setMarket_tradable_restriction(int market_tradable_restriction) {
        this.market_tradable_restriction = market_tradable_restriction;
    }

    public int getMarket_tradable_restriction() {
        return this.market_tradable_restriction;
    }

    public void setMarketable(int marketable) {
        this.marketable = marketable;
    }

    public int getMarketable() {
        return this.marketable;
    }

    public void setMarket_buy_country_restriction(String market_buy_country_restriction) {
        this.market_buy_country_restriction = market_buy_country_restriction;
    }

    public String getMarket_buy_country_restriction() {
        return this.market_buy_country_restriction;
    }
}
