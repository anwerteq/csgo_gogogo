package com.xiaojuzi.st.csgo.entity;


import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Data
@ToString
@Entity
@IdClass(BuffPriceHistoryPk.class)
@Table(name = "steamprice_history")
public class SteamPriceHistory {

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
