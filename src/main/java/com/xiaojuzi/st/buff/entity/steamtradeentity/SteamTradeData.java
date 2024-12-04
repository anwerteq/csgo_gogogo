/**
  * Copyright 2023 json.cn
  */
package com.xiaojuzi.st.buff.entity.steamtradeentity;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2023-05-25 20:10:10
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class SteamTradeData {

    private int appid;
    private int bot_age;
    private String bot_age_icon;
    private String bot_avatar;
    private String bot_extra_info;
    private int bot_level;
    private String bot_level_background_color;
    private String bot_level_background_image;
    private String bot_name;
    private long bot_steam_created_at;
    private int create_count_up;
    private long created_at;
    private String game;
    private Goods_infos goods_infos;
    private String id;
    private List<Items_to_trade> items_to_trade;
    private int state;
    private String text;
    private String title;
    private String trace_url;
    private String tradeofferid;
    private int type;
    private String url;
    private String verify_code;

}
