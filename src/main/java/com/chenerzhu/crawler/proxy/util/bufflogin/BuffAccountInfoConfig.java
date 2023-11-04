package com.chenerzhu.crawler.proxy.util.bufflogin;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 网易buff账号信息
 */
@Configuration
@ConfigurationProperties(prefix = "buff")
@Data
public class BuffAccountInfoConfig {


    private List<String> account_information;
    private String buff_cookie;


}
