package com.chenerzhu.crawler.proxy.buff.service.deliverOrder;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2023-10-07 22:49:32
 *
 * @author www.ecjson.com
 * @website http://www.ecjson.com/json2java/
 */
public class Data {

    private int appid;
    @JsonProperty("bot_age")
    private int botAge;
    @JsonProperty("bot_age_icon")
    private String botAgeIcon;
    @JsonProperty("bot_avatar")
    private String botAvatar;
    @JsonProperty("bot_extra_info")
    private String botExtraInfo;
    @JsonProperty("bot_level")
    private int botLevel;
    @JsonProperty("bot_level_background_color")
    private String botLevelBackgroundColor;
    @JsonProperty("bot_level_background_image")
    private String botLevelBackgroundImage;
    @JsonProperty("bot_name")
    private String botName;
    @JsonProperty("bot_steam_created_at")
    private int botSteamCreatedAt;
    @JsonProperty("create_count_up")
    private int createCountUp;
    @JsonProperty("created_at")
    private int createdAt;
    private String game;
    @JsonProperty("goods_infos")
    private GoodsInfos goodsInfos;
    private String id;
    @JsonProperty("items_to_trade")
    private List<ItemsToTrade> itemsToTrade;
    private int state;
    private String text;
    private String title;
    @JsonProperty("trace_url")
    private String traceUrl;
    private String tradeofferid;
    private int type;
    private String url;
    @JsonProperty("verify_code")
    private Date verifyCode;

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public int getBotAge() {
        return botAge;
    }

    public void setBotAge(int botAge) {
        this.botAge = botAge;
    }

    public String getBotAgeIcon() {
        return botAgeIcon;
    }

    public void setBotAgeIcon(String botAgeIcon) {
        this.botAgeIcon = botAgeIcon;
    }

    public String getBotAvatar() {
        return botAvatar;
    }

    public void setBotAvatar(String botAvatar) {
        this.botAvatar = botAvatar;
    }

    public String getBotExtraInfo() {
        return botExtraInfo;
    }

    public void setBotExtraInfo(String botExtraInfo) {
        this.botExtraInfo = botExtraInfo;
    }

    public int getBotLevel() {
        return botLevel;
    }

    public void setBotLevel(int botLevel) {
        this.botLevel = botLevel;
    }

    public String getBotLevelBackgroundColor() {
        return botLevelBackgroundColor;
    }

    public void setBotLevelBackgroundColor(String botLevelBackgroundColor) {
        this.botLevelBackgroundColor = botLevelBackgroundColor;
    }

    public String getBotLevelBackgroundImage() {
        return botLevelBackgroundImage;
    }

    public void setBotLevelBackgroundImage(String botLevelBackgroundImage) {
        this.botLevelBackgroundImage = botLevelBackgroundImage;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public int getBotSteamCreatedAt() {
        return botSteamCreatedAt;
    }

    public void setBotSteamCreatedAt(int botSteamCreatedAt) {
        this.botSteamCreatedAt = botSteamCreatedAt;
    }

    public int getCreateCountUp() {
        return createCountUp;
    }

    public void setCreateCountUp(int createCountUp) {
        this.createCountUp = createCountUp;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public GoodsInfos getGoodsInfos() {
        return goodsInfos;
    }

    public void setGoodsInfos(GoodsInfos goodsInfos) {
        this.goodsInfos = goodsInfos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ItemsToTrade> getItemsToTrade() {
        return itemsToTrade;
    }

    public void setItemsToTrade(List<ItemsToTrade> itemsToTrade) {
        this.itemsToTrade = itemsToTrade;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTraceUrl() {
        return traceUrl;
    }

    public void setTraceUrl(String traceUrl) {
        this.traceUrl = traceUrl;
    }

    public String getTradeofferid() {
        return tradeofferid;
    }

    public void setTradeofferid(String tradeofferid) {
        this.tradeofferid = tradeofferid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(Date verifyCode) {
        this.verifyCode = verifyCode;
    }

}
