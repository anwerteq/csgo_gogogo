package com.xiaojuzi.buff.service.deliverOrder;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Auto-generated: 2023-10-07 22:49:32
 *
 * @website http://www.ecjson.com/json2java/
 */
@lombok.Data
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
    private String verifyCode;

}
