/**
  * Copyright 2023 json.cn 
  */
package com.xiaojuzi.st.buff.entity.steamInventory;
import lombok.Data;

import java.util.Date;

@Data
public class ManualPlusAssets {

    private String game;
    private Date market_hash_name;
    private int contextid;
    private String assetid;
    private String classid;
    private String instanceid;
    private long goods_id;
    private String price;
    private String income;
    private boolean has_market_min_price;

}