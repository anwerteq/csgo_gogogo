package com.chenerzhu.crawler.proxy.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author lbb
 * @date 2023/7/27 16:11
 * @description
 */
@Slf4j
@Configuration
public class MtdtDataSourceConfig {
    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.type}")
    private String dbName;

    @Bean("connectionProvider")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ObjectProvider<Connection> connectionProvider(){
        return new ObjectProvider<Connection>() {
            Connection conn=null;

            @Override
            public Connection getObject() throws BeansException {
                try {
                    Class.forName(driver);
                    //连接数据库
                    conn = DriverManager.getConnection(url, userName, password);
                    conn.setAutoCommit(false);
                    log.info("元数据数据库连接成功！--{}" , url);
                    return conn;
                }catch(Exception se) {
                    //连接失败
                    log.info("元数据数据库连接失败！--{}  失败原因为： {}" ,url,se.getMessage());
                }
                return conn;
            }

            @Override
            public Connection getObject(Object... objects) throws BeansException {
                return null;
            }

            @Override
            public Connection getIfAvailable() throws BeansException {
                return this.conn;
            }

            @Override
            public Connection getIfUnique() throws BeansException {
                return null;
            }
        };
    }
}
