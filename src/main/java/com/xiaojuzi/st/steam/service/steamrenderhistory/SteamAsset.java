package com.xiaojuzi.st.steam.service.steamrenderhistory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.StringJoiner;

/**
 * Auto-generated: 2023-11-06 21:33:51
 *
 * @author www.pcjson.com
 * @website http://www.pcjson.com/json2java/
 */
@Data
public class SteamAsset {

    /**
     * 获取磨损度的url
     */
    String link;
    /**
     * 购买的美金金额
     */
    String price;
    /**
     * 磨损度
     */
    String painwear;
    /**
     *
     */
    String instanceidAndPainwear;
    private int currency;
    private int appid;
    private String contextid;
    private String id;
    private String classid;
    private String instanceid;
    private String amount;
    private int status;
    @JsonProperty("original_amount")
    private String originalAmount;
    @JsonProperty("unowned_id")
    private String unownedId;
    @JsonProperty("unowned_contextid")
    private String unownedContextid;
    @JsonProperty("background_color")
    private String backgroundColor;
    @JsonProperty("icon_url")
    private String iconUrl;
    @JsonProperty("icon_url_large")
    private String iconUrlLarge;
    private List<Descriptions> descriptions;
    private int tradable;
    private List<Actions> actions;
    private List fraudwarnings;
    private String name;
    @JsonProperty("name_color")
    private String nameColor;
    private String type;
    @JsonProperty("market_name")
    private String marketName;
    @JsonProperty("market_hash_name")
    private String marketHashName;
    @JsonProperty("market_actions")
    private List<MarketActions> marketActions;
    private int commodity;
    @JsonProperty("market_tradable_restriction")
    private int marketTradableRestriction;
    private int marketable;
    @JsonProperty("app_icon")
    private String appIcon;
    private int owner;

    /**
     * 实例id和磨损度的值,buff饰品和steam饰品的关联值
     *
     * @return
     */
    public String getInstanceidAndPainwear() {
        StringJoiner sj = new StringJoiner("-");
        sj.add(instanceid);
        sj.add(painwear);
        return sj.toString();
    }
}
