package com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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

    private Double steam_price;

    String market_hash_name;

    String sell_min_price;

    String painwear;

    String action_link;

    AssetExtra asset_extra;

    String name;

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


    public Boolean cehck_isSale_remark_cost() {
        if (ObjectUtil.isNull(asset_extra)) {
            return true;
        }
        String remark = asset_extra.getRemark();
        if (StrUtil.isEmpty(remark)) {
            return true;
        }
        if (remark.contains("成本")) {
            String cost = remark.split("成本:")[1].split("元")[0];
            Double costPrice = Double.valueOf(cost);
            Double sell_min_priceD = Double.valueOf(sell_min_price);
            Boolean isSale = costPrice * 1.05 < sell_min_priceD;
            return isSale;
        }
        return false;
    }

    public Boolean check4Stickers() {

        return true;
    }

    /**
     * 实例id和磨损度的值,buff饰品和steam饰品的关联值
     *
     * @return
     */
    public String getInstanceidAndPainwear() {
        StringJoiner sj = new StringJoiner("-");
        sj.add(asset_info.getInstanceid());
        sj.add(painwear);
        return sj.toString();
    }
}
