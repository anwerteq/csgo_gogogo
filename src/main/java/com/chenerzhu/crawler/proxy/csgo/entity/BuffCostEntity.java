package com.chenerzhu.crawler.proxy.csgo.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.StringJoiner;

/**
 * buff购买记录表
 */
@Data
@ToString
@Entity
@Table(name = "buff_cost")
public class BuffCostEntity {

    @Id
    private String cdkey_id;

    String name;
    /**
     * buff购买信息是否和steam库存配置过
     */
    int is_mate;


    /**
     * buff购买成本
     */
    double buff_cost;


    String hash_name;
    String assetid;

    String classid;

    String instanceid;



    /**
     * steam销售到手金额,人民币：分
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


    @Transient
    public void refreashCdkey_id() {
        StringJoiner sj = new StringJoiner("-");
        sj.add(assetid);
        sj.add(classid);
        sj.add(instanceid);
        setCdkey_id(sj.toString());
    }

}
