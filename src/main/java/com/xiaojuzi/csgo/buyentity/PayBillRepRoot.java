/**
 * Copyright 2023 json.cn
 */
package com.xiaojuzi.csgo.buyentity;

/**
 * 支付后返回的数据
 */
public class PayBillRepRoot {

    private String code;
    private PayBillRepData payBillRepData;
    private String msg;
    public void setCode(String code) {
         this.code = code;
     }
     public String getCode() {
         return code;
     }

    public void setData(PayBillRepData payBillRepData) {
         this.payBillRepData = payBillRepData;
     }
     public PayBillRepData getData() {
         return payBillRepData;
     }

    public void setMsg(String msg) {
         this.msg = msg;
     }
     public String getMsg() {
         return msg;
     }

}
