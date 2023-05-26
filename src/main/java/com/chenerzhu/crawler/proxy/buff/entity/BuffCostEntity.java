package com.chenerzhu.crawler.proxy.buff.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * buff购买记录表
 */
@Data
@ToString
@Entity
@Table(name = "buff_cost")
public class BuffCostEntity {

    @Id
    private String costId;

    String name;
    String hash_name;

    long assetid;

    long classid;


    /**
     * buff购买成本
     */
    double buff_cost;

    /**
     * steam销售到手金额
     */
    double returned_money;

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
    Date createTime;

    /**
     * 更新时间
     */
    Date updateTime;

}
