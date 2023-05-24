/**
  * Copyright 2023 json.cn 
  */
package com.chenerzhu.crawler.proxy.pool.csgo.steamentity.InventoryEntity;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2023-05-22 14:41:55
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class Descriptions {

    private int appid;
    private String classid;
    private String instanceid;
    private int currency;
    private String background_color;
    private String icon_url;
    private String icon_url_large;
    private List<Descriptions> descriptions;
    private int tradable;
    private List<Actions> actions;
    private List<Owner_descriptions> owner_descriptions;
    private String name;
    private String name_color;
    private String type;
    private String market_name;
    private String market_hash_name;
    private List<Market_actions> market_actions;
    private int commodity;
    private int market_tradable_restriction;
    private int marketable;
    private List<Tags> tags;
}