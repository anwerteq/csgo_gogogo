package com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity;

import lombok.Data;

import java.util.StringJoiner;

@Data
public class Items
{
    private int appid;

    private Asset_info asset_info;

    private Bundle_info bundle_info;

    private String buyer_cancel_timeout;

    private boolean buyer_cookie_invalid;

    private String buyer_id;

    private int buyer_pay_time;

    private int buyer_send_offer_timeout;

    private boolean can_replace_asset;

    private String coupon_info;

    private String coupon_infos;

    private int created_at;

    private int deliver_expire_timeout;

    private String error_text;

    private String fail_confirm;

    private String fee;

    private String game;

    private int goods_id;

    private boolean has_bargain;

    private boolean has_sent_offer;

    private String id;

    private String income;

    private boolean is_seller_asked_to_send_offer;

    private int mode;

    private String original_price;

    private int pay_expire_timeout;

    private int pay_method;

    private String pay_method_text;

    private String price;

    private String price_with_pay_fee;

    private int progress;

    private int receive_expire_timeout;

    private String sell_order_id;

    private boolean seller_can_cancel;

    private boolean seller_cookie_invalid;

    private String seller_id;

    private String state;

    private String state_text;

    private String trade_offer_trace_url;

    private String trade_offer_url;

    private String tradeofferid;

    private String transact_time;

    private int type;

    private int updated_at;

    String market_hash_name;

    String sell_min_price;

    /**
     * 获取饰品的唯一值
     *
     * @return
     */
    public String getAssetidClassidInstanceid() {
        StringJoiner sj = new StringJoiner("-");
        sj.add(asset_info.getAssetid());
        sj.add(asset_info.getClassid());
        sj.add(asset_info.getInstanceid());
        return sj.toString();
    }

    public String getClassidInstanceid() {
        StringJoiner sj = new StringJoiner("-");
        sj.add(asset_info.getClassid());
        sj.add(asset_info.getInstanceid());
        return sj.toString();
    }

}
