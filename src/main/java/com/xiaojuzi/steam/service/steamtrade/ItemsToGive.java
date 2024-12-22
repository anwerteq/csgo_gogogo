package com.xiaojuzi.steam.service.steamtrade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.StringJoiner;

/**
 * Auto-generated: 2023-10-08 15:18:45
 *
 * @author www.ecjson.com
 * @website http://www.ecjson.com/json2java/
 */

@Data
public class ItemsToGive {

    private String appid;
    private String contextid;
    private String assetid;
    private String classid;
    private String instanceid;
    private String amount;
    private boolean missing;
    @JsonProperty("est_usd")
    private String estUsd;


    /**
     * 一个装备的唯一值
     *
     * @return
     */
    public String getOnlyKey() {
        StringJoiner sj = new StringJoiner("-");
        sj.add(appid);
        sj.add(assetid);
        sj.add(classid);
        sj.add(contextid);
        sj.add(instanceid);
        return sj.toString();
    }
}
