package com.chenerzhu.crawler.proxy.steam.entity;

import javax.persistence.Id;
import java.util.Date;

/**
 * steam商品购买统计图
 */
public class SteamCostEntity {

    @Id
    private String costId;

    String name;
    /**
     * steam购买信息是否和buff销售信息挂钩
     */
    int is_mate;


    /**
     * steam购买成本
     */
    double steam_cost;


    String hash_name;
    long assetid;

    long classid;



    /**
     * buff销售到手金额,（美分）
     */
    int returned_money;


    /**
     * 利润金额
     */
    double profit_money;


    /**
     * （0）下订单，（1）支付成功 （2）确定收货成功 （3）售卖成功
     */
    Integer buy_status;

    /**
     * 创建时间
     */
    Date create_time;

    /**
     * 更新时间
     */
    Date update_time;

}
