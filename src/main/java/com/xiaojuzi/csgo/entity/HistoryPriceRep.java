/**
  * Copyright 2023 jb51.net
  */
package com.xiaojuzi.csgo.entity;

/**
 * Auto-generated: 2023-05-15 21:42:53
 *
 * @website http://tools.jb51.net/code/json2javabean
 */
@lombok.Data
public class HistoryPriceRep {

    private String code;
    private HistoryPrice data;
    private String msg;
    public void setCode(String code) {
         this.code = code;
     }
     public String getCode() {
         return code;
     }


}
