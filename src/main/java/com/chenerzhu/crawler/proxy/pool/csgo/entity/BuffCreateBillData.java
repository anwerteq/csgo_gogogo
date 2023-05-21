package com.chenerzhu.crawler.proxy.pool.csgo.entity;

import lombok.Data;

import java.util.List;
@Data
public class BuffCreateBillData
{
    private String discounted_price;

    private String original_price;

    private List<PayMethods> pay_methods;

    private String payment_tips;


}
