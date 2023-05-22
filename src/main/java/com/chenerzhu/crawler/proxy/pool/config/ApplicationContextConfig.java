package com.chenerzhu.crawler.proxy.pool.config;


import com.chenerzhu.crawler.proxy.pool.csgo.config.SSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


/**
 * @author Yeafel
 * 2021/3/13 23:45
 * Do or Die,To be a better man!
 */
@Configuration
public class ApplicationContextConfig {

    @Bean
    public RestTemplate getRestTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SSL factory = new SSL();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(15000);
        return factory;
    }
}
