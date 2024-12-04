package com.xiaojuzi.st.csgo.BuffBuyItemEntity;

import lombok.Data;

import java.util.List;

@Data
public class BuyOrderHistoryRoot {
    private String code;

    private List<BuyOrderHistoryData> data;

    private String msg;

}
