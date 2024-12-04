package com.xiaojuzi.st.csgofloat.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * csgo_float用户信息
 */
@Data
@ToString
@Entity
@Table(name = "float_user")
public class FloatUser implements Serializable {

    /**
     * cookie
     */
    String cookie;
    /**
     * steam账号
     */
    @Id
    private String name;
    /**
     * 密码
     */
    @Id
    private String password;


}
