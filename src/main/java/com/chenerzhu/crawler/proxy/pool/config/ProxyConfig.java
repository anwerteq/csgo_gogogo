package com.chenerzhu.crawler.proxy.pool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created Date by 2020/4/7 0007.
 *
 * @author Parker
 */
@Configuration
@PropertySource("classpath:config/proxyip.properties")
@ConfigurationProperties(prefix = "proxy", ignoreUnknownFields = false)
@Component
@Data
public class ProxyConfig {

    private List<String> addrList;

    private String delayTime;

}
