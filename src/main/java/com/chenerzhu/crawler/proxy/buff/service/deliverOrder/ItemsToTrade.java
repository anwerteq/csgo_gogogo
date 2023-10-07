package com.chenerzhu.crawler.proxy.buff.service.deliverOrder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Auto-generated: 2023-10-07 22:49:32
 *
 * @author www.ecjson.com
 * @website http://www.ecjson.com/json2java/
 */
public class ItemsToTrade {

    private int appid;
    private String assetid;
    private String classid;
    private int contextid;
    @JsonProperty("goods_id")
    private int goodsId;
    private String instanceid;

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public String getAssetid() {
        return assetid;
    }

    public void setAssetid(String assetid) {
        this.assetid = assetid;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public int getContextid() {
        return contextid;
    }

    public void setContextid(int contextid) {
        this.contextid = contextid;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(String instanceid) {
        this.instanceid = instanceid;
    }

}
