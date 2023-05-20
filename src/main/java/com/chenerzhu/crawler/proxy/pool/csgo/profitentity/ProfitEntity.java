package com.chenerzhu.crawler.proxy.pool.csgo.profitentity;

import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffPriceHistoryPk;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * 推荐购买记录
 */
@Data
@ToString
@Entity
@Table(name = "profit")
public class ProfitEntity {

    @Id
    private long item_id;
    //物品名称
    String name;
    //steam价格购买
    private String steam_price_cny;
    //steam真实价格购买
    String  in_fact_steam_price_cny;
    //当前buff售出最低
    private String sell_min_price;
    //buff快速成交最低
    private String quick_price;
    //buff卖的数量
    String sell_num;
    //利率%
    private String interest_rate;
}
