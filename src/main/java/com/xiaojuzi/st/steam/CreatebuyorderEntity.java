package com.xiaojuzi.st.steam;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * steam提交订单地址
 */
@Data
@Configuration
public class CreatebuyorderEntity {
    private String sessionid;
    private String currency = "1";
    private String appid = "730";
    /**
     * 商品hash名字
     */
    private String market_hash_name;

    /**
     * 商品总价=单价*商品数量 )($0.02 = 2)
     */
    private String price_total;
    /**
     * 商品数量
     */
    private String quantity = "13";
    private String first_name = "Ke";
    private String last_name = "Le Le";
    private String billing_address_two = "1626  Monroe Street";
    private String billing_country = "US";
    private String billing_address = "1625  Monroe Street";
    private String billing_city = "Houston";
    private String billing_state = "AL";
    private String billing_postal_code = "77030";
    private String save_my_address = "1";

}
