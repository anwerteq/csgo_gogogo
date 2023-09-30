/**
 * Copyright 2023 json.cn
 */
package com.chenerzhu.crawler.proxy.buff.entity.steamInventory;
import com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity.Assets;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;



@Data
public class ManualPlusRoot {

    private String game = "csgo";
    private List<Assets> assets = new ArrayList<>();

}
