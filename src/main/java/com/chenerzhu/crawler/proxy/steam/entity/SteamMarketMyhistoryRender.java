package com.chenerzhu.crawler.proxy.steam.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * steam市场交易记录
 */

@Data
@ToString
@Entity
@Table(name = "steam_market_myhistory_render")
public class SteamMarketMyhistoryRender {

    @Id
    private String historyRowId;

    /**
     * 类型： buy or sell
     */
    private String tradingType;


    private String tradingDate;

    private String assetid;

    private String classid;

    private String name;

    private String hashName;

    private Double usd;





}
