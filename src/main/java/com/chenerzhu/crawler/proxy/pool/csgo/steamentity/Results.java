
package com.chenerzhu.crawler.proxy.pool.csgo.steamentity;
public class Results
{
    private String name;

    private String hash_name;

    private int sell_listings;

    private int sell_price;

    private String sell_price_text;

    private String app_icon;

    private String app_name;

    private Asset_description asset_description;

    private String sale_price_text;

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setHash_name(String hash_name){
        this.hash_name = hash_name;
    }
    public String getHash_name(){
        return this.hash_name;
    }
    public void setSell_listings(int sell_listings){
        this.sell_listings = sell_listings;
    }
    public int getSell_listings(){
        return this.sell_listings;
    }
    public void setSell_price(int sell_price){
        this.sell_price = sell_price;
    }
    public int getSell_price(){
        return this.sell_price;
    }
    public void setSell_price_text(String sell_price_text){
        this.sell_price_text = sell_price_text;
    }
    public String getSell_price_text(){
        return this.sell_price_text;
    }
    public void setApp_icon(String app_icon){
        this.app_icon = app_icon;
    }
    public String getApp_icon(){
        return this.app_icon;
    }
    public void setApp_name(String app_name){
        this.app_name = app_name;
    }
    public String getApp_name(){
        return this.app_name;
    }
    public void setAsset_description(Asset_description asset_description){
        this.asset_description = asset_description;
    }
    public Asset_description getAsset_description(){
        return this.asset_description;
    }
    public void setSale_price_text(String sale_price_text){
        this.sale_price_text = sale_price_text;
    }
    public String getSale_price_text(){
        return this.sale_price_text;
    }
}
