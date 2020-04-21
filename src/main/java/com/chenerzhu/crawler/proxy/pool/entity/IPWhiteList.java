package com.chenerzhu.crawler.proxy.pool.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author parker
 *
 * IP白名单
 * @create 2018-08-29 21:00
 **/
@Data
@ToString
@Entity
@Table(name = "ip_white_list")
public class IPWhiteList implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JSONField(serialize = false)
    private Long id;

    /**
     * 白名单IP
     */
    @Column(name="ip" ,nullable=false)
    private String ip;

    /**
     * 是否可用
     */
    @Column(name="is_usable" ,nullable=false)
    private Integer isUsable;


}