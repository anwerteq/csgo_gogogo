package com.xiaojuzi.st.buff.service.deliverOrder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.StringJoiner;

/**
 * Auto-generated: 2023-10-07 22:49:32
 *
 * @author www.ecjson.com
 * @website http://www.ecjson.com/json2java/
 */
@Data
public class ItemsToTrade {

    private String appid;
    private String assetid;
    private String classid;
    private String contextid;
    @JsonProperty("goods_id")
    private String goodsId;
    private String instanceid;

    private String tradeofferid;


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
