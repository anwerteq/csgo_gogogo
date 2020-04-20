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
@Entity
@Table(name = "proxy_config")
public class ProxyConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JSONField(serialize = false)
    private long id;

    /**
     * 验证地址
     */
    @Column(name="validate_url" ,nullable=false)
    private String validateUrl;

    /**
     * 验证次数
     *
     * （默认为3次验证），如果代理IP存活时间较短 可该小验证次数
     */
    @Column(name="validate_count" ,nullable=false)
    private Integer validateCount;

    /**
     * 延迟时间
     *
     * 如果为0 则开启随机
     */
    @Column(name="delay_time" ,nullable=false)
    private Integer delayTime;

    /**
     * 私有接口-认证用户名
     */
    @Column(name="private_username" ,nullable=false)
    private String privateUserName;


    /**
     * 私有接口-认证用户密码
     */
    @Column(name="private_password" ,nullable=false)
    private String privatePassword;
}