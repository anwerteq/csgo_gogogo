/**
  * Copyright 2023 json.cn 
  */
package com.chenerzhu.crawler.proxy.buff.entity.steamInventory;


import lombok.Data;

@Data
public class SteamInventoryRoot {

    private String code;
    private SteamInventoryData data;
    private String msg;


}