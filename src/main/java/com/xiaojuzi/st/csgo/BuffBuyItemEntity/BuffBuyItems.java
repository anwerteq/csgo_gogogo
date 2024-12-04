package com.xiaojuzi.st.csgo.BuffBuyItemEntity;

import lombok.Data;

import java.util.List;

@Data
public class BuffBuyItems {
    private boolean allow_bargain;

    private int appid;

    private Asset_info asset_info;

    private String background_image_url;

    private boolean bookmarked;

    private boolean can_bargain;

    private boolean can_use_inspect_trn_url;

    private String cannot_bargain_reason;

    private int created_at;

    private String description;

    private int featured;

    private String fee;

    private String game;

    private int goods_id;

    private String id;

    private String img_src;

    private String income;

    private String lowest_bargain_price;

    private int mode;

    /**
     * 售卖的价格
     */
    private String price;

    private int recent_average_duration;

    private int recent_deliver_rate;

    private int state;


    String name;
    String hash_name;



    /**
     * 支持的支付方式
     */
    private List<Integer> supported_pay_methods;

    private String tradable_cooldown;

    private int updated_at;

    private String user_id;

}
