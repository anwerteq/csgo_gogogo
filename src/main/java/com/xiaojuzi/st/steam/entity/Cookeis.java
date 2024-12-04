package com.xiaojuzi.st.steam.entity;


import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * cookies记录
 */
@Data
@ToString
@Entity
@Table(name = "cookeis")
public class Cookeis {


    @Id
    private long number;

    @Column(columnDefinition = "text")
    private String buff_cookie;


    @Column(columnDefinition = "text")
    private String steam_cookie;

    /**
     * 0：暂停使用，1：开启使用
     */
    private String is_top;
}
