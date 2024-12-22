/**
  * Copyright 2023 bejson.com
  */
package com.xiaojuzi.csgo.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * Auto-generated: 2023-05-14 0:18:12
 *
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@ToString
@Entity
@IdClass(TagPk.class)
@Table(name = "tag")
public class Tag {


    private String category;

    @Id//这个注解很重要，是联合主键其中的一个
    @Column(name = "id", nullable = false,unique = false)
    private int id;

    @Id//这个注解很重要，是联合主键其中的一个
    @Column(name = "item_id", nullable = false,unique = false)
    private long item_id;
    private String internal_name;
    private String localized_name;

}

