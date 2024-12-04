/**
  * Copyright 2023 json.cn
  */
package com.xiaojuzi.st.buff.entity.steamtradeentity;
import java.util.List;

/**
 * Auto-generated: 2023-05-25 20:10:10
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class SteamTradeRoot {

    private String code;
    private List<SteamTradeData> data;
    private String msg;
    public void setCode(String code) {
         this.code = code;
     }
     public String getCode() {
         return code;
     }

    public void setData(List<SteamTradeData> data) {
         this.data = data;
     }
     public List<SteamTradeData> getData() {
         return data;
     }

    public void setMsg(String msg) {
         this.msg = msg;
     }
     public String getMsg() {
         return msg;
     }

}
