package com.chenerzhu.crawler.proxy.pool.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author parker
 * @create 2020年4月20日14:51:17
 **/
@Data
@ToString
@Entity
@Table(name = "proxy_api")
public class ProxyApi implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JSONField(serialize = false)
    private long id;

    @Column(name="ip_api" ,nullable=false)
    private String ipApi;

    @Column(name="type" ,nullable=false)
    private String type;

}