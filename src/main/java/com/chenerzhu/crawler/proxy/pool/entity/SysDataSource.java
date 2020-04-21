/**
 * Copyright &copy; Edwin All rights reserved.
 */
package com.chenerzhu.crawler.proxy.pool.entity;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 多数据源Entity
 * @author parker
 * @version 2017-07-27
 */
@Data
public class SysDataSource implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/** 数据库用户名 */
	private String dbUserName = "proxy";
    /** 数据库密码 */
	private String dbPassword = "PROXY!@#123";
    /** 数据库链接 */
	private String dbUrl = "jdbc:mysql://39.97.162.240:3306/proxy?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=UTC";
    /** 数据库驱动类 */
	private String dbDriver = "com.mysql.cj.jdbc.Driver";

}