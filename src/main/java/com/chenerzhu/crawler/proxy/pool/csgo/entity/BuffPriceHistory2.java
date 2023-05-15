package com.chenerzhu.crawler.proxy.pool.csgo.entity;


import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@ToString
@Entity
@IdClass(BuffPriceHistoryPk.class)
@Table(name = "buff_price_history2")
public class BuffPriceHistory2 {

    @Id//这个注解很重要，是联合主键其中的一个
    private long item_id;


    @Id
    private long time_stamp;

    private Double price;

    /**
     * 最后更新时间
     */
    private long up_time_stamp;


}
