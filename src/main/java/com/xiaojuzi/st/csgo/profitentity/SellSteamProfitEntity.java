package com.xiaojuzi.st.csgo.profitentity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 在steam上售卖
 */
@Data
@ToString
@Entity
@Table(name = "sell_steam_profit")
public class SellSteamProfitEntity {

    @Id
    private String item_id;
    //物品名称
    String name;
    String hash_name;
    //buff价格购买
    private double buff_price;

    //steam上销售价格 人民币价格
    private String sell_steam_price;

    //steam税后的价格 人民币价格
    private double in_fact_sell_steam_price;

    //buff卖的数量
    long sell_num;
    //折扣%
    private String interest_rate;

    private Date up_date;
}
