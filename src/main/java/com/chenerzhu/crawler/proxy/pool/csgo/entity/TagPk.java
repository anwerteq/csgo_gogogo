package com.chenerzhu.crawler.proxy.pool.csgo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
 public class TagPk implements Serializable {

    private static final long serialVersionUID = -1570834456846591727L;
    private int id;
    private long item_id;
}
