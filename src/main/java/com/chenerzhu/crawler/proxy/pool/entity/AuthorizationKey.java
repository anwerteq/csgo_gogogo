package com.chenerzhu.crawler.proxy.pool.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author parker
 * @create 2020年4月20日14:51:17
 **/
@Data
@ToString
//@Entity
@Table(name = "authorization_key")
public class AuthorizationKey implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JSONField(serialize = false)
    private long id;

    @Column(name="key" ,nullable=false)
    private String key;

    @Column(name="is_usable" ,nullable=false)
    private String is_usable;

    @Column(name="contact")
    private String contact;

    @Column(name="remarks" )
    private String remarks;

}