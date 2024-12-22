/**
 * Copyright 2023 json.cn
 */
package com.xiaojuzi.csgo.steamentity.InventoryEntity;

/**
 * Auto-generated: 2023-05-22 14:41:55
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */

import lombok.Data;

import javax.persistence.Transient;
import java.util.StringJoiner;

/**
 * 库存商品类信息
 */

@Data
public class Assets {

    private int appid = 730;
    private String contextid = "2";
    private String assetid;
    private String classid;
    private String instanceid;
    private String amount;

    String game = "csgo";

    Boolean has_market_min_price;

    String goods_id;

    String market_hash_name;

    /**
     * 收入
     */
    String income;

    String price;

    String cdkey_id;


    /**
     * 获取饰品的唯一值
     *
     * @return
     */

    @Transient
    public String assetidClassidInstanceid() {
        StringJoiner sj = new StringJoiner("-");
        sj.add(assetid);
        sj.add(classid);
        sj.add(instanceid);
        return sj.toString();
    }

}
