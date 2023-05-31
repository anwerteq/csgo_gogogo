package com.chenerzhu.crawler.proxy.steam.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * steam商品购买统计图
 */

@Data
@ToString
@Entity
@Table(name = "steam_cost")
public class SteamCostEntity {

    @Id
    private String costId;

    String name;
    /**
     * steam购买信息是否和buff销售信息挂钩
     */
    int is_mate;


    /**
     * steam购买成本,美分
     */
    double steam_cost;


    String hash_name;
    String assetid;

    String classid;



    /**
     * buff销售到手金额,（美分）
     */
    int returned_money;


    /**
     * 利润金额
     */
    double profit_money;


    /**
     * （0）下订单 （1）steam上架匹配过 （2）buff上架匹配过
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
