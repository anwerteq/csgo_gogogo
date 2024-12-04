/**
 * Copyright 2023 json.cn
 */
package com.xiaojuzi.st.buff.entity.steamInventory;
import com.xiaojuzi.st.csgo.steamentity.InventoryEntity.Assets;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;



@Data
public class ManualPlusRoot {

    private String game = "csgo";
    private List<Assets> assets = new ArrayList<>();

}
