package com.chenerzhu.crawler.proxy.csgo.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
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
    @Column(length = 155)
    private String id;
    @Column(name = "cdkey_id")
    private String cdkeyId;

    @Column(length = 126)
    String name;
    /**
     * buff购买信息是否和steam库存配置过
     */
    @Column
    int is_mate;


    /**
     * buff购买成本
     */
    @Column
    double buff_cost;

    @Column(length = 126)
    String hash_name;
    @Column(length = 126)
    String assetid;

    @Column(length = 126)
    String classid;
    @Column(length = 126)
    String instanceid;

    /**
     * 状态文本
     */
    @Column
    String statucText;



    /**
     * steam销售到手金额,人民币：分
     */
    @Column(length = 126)
    int returned_money;


    /**
     * 利润金额
     */

    @Column(length = 126)
    double profit_money;


    /**
     * （0）下订单，（1）支付成功 （2）确定收货成功 （3）售卖成功
     */
    @Column(length = 126)
    Integer buy_status;

    /**
     * 创建时间
     */
    @Column
    Date create_time;


    /**
     * 手机号
     */
    @Column
    String mobileNumber;


    /**
     * 手机号
     */
    @Column
    int number;

    /**
     * 更新时间
     */
    @Column
    Date update_time;


    @Transient
    public void refreashCdkey_id() {
        StringJoiner sj = new StringJoiner("-");
        sj.add(assetid);
        sj.add(classid);
        sj.add(instanceid);
        setCdkeyId(sj.toString());
    }

}
