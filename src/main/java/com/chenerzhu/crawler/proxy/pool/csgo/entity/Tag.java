/**
  * Copyright 2023 bejson.com
  */
package com.chenerzhu.crawler.proxy.pool.csgo.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Auto-generated: 2023-05-14 0:18:12
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@ToString
@Entity
@Table(name = "tag")
public class Tag {

    private String category;
    @Id
    private int id;
    private long item_id;
    private String internal_name;
    private String localized_name;

}
