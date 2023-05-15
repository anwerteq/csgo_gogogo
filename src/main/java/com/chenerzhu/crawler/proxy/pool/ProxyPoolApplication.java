package com.chenerzhu.crawler.proxy.pool;

import com.chenerzhu.crawler.proxy.pool.csgo.entity.ProductList;
import com.chenerzhu.crawler.proxy.pool.service.impl.ProxyIpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@ServletComponentScan("com.chenerzhu.crawler.proxy.pool.listener")
@EnableFeignClients
public class ProxyPoolApplication {


	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(ProxyPoolApplication.class, args);
	}
}
