/**
  * Copyright 2023 bejson.com
  */
package com.xiaojuzi.csgo.entity;
import java.util.List;

/**
 * Auto-generated: 2023-05-14 0:18:12
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Data {

    private List<ItemGoods> itemGoods;
    private int page_num;
    private int page_size;
    private int total_count;
    private int total_page;
    public void setItems(List<ItemGoods> itemGoods) {
         this.itemGoods = itemGoods;
     }
     public List<ItemGoods> getItems() {
         return itemGoods;
     }

    public void setPage_num(int page_num) {
         this.page_num = page_num;
     }
     public int getPage_num() {
         return page_num;
     }

    public void setPage_size(int page_size) {
         this.page_size = page_size;
     }
     public int getPage_size() {
         return page_size;
     }

    public void setTotal_count(int total_count) {
         this.total_count = total_count;
     }
     public int getTotal_count() {
         return total_count;
     }

    public void setTotal_page(int total_page) {
         this.total_page = total_page;
     }
     public int getTotal_page() {
         return total_page;
     }

}
