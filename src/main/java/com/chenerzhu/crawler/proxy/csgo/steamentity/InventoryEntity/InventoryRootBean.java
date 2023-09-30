/**
 * Copyright 2023 json.cn
 */
package com.chenerzhu.crawler.proxy.csgo.steamentity.InventoryEntity;

import java.util.List;

/**
 * Auto-generated: 2023-05-22 14:41:55
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class InventoryRootBean {

    private List<Assets> assets;
    private List<Descriptions> descriptions;
    private int total_inventory_count;
    private int success;
    private int rwgrsn;
    public void setAssets(List<Assets> assets) {
         this.assets = assets;
     }
     public List<Assets> getAssets() {
         return assets;
     }

    public void setDescriptions(List<Descriptions> descriptions) {
         this.descriptions = descriptions;
     }
     public List<Descriptions> getDescriptions() {
         return descriptions;
     }

    public void setTotal_inventory_count(int total_inventory_count) {
         this.total_inventory_count = total_inventory_count;
     }
     public int getTotal_inventory_count() {
         return total_inventory_count;
     }

    public void setSuccess(int success) {
         this.success = success;
     }
     public int getSuccess() {
         return success;
     }

    public void setRwgrsn(int rwgrsn) {
         this.rwgrsn = rwgrsn;
     }
     public int getRwgrsn() {
         return rwgrsn;
     }

}
