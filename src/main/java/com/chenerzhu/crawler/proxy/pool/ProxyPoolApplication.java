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
		//csgo请求头数据
		Map<String, String> map = new HashMap() {
			{
				put("sec-ch-ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Google Chrome\";v=\"114\"");
				put("Accept", "application/json, text/javascript, */*; q=0.07");
				put("X-Requested-With", "XMLHttpRequest");
				put("sec-ch-ua-mobile", "?0");
				put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
				put("sec-ch-ua-platform", "\"Windows\"");
				put("Sec-Fetch-Site", "same-origin");
				put("Sec-Fetch-Mode", "cors");
				put("Sec-Fetch-Dest", "empty");
				put("Referer", "http://buff.163.com/market/csgo");
//                put("Cookie", "Device-Id=8qNWoqIr0y4cISb0LBIS; client_id=YlNlv5mS1lJdkqcidGXcwQ; csrf_token=IjVjZWYzODRmMzU0NjBhZDEyMDc4NmE4ODc1OTMwMzQ5YzEzNzg2OTgi.F0IlFA.CqSOXlLH17C7l9v8Zl8GC2NZHew");
			}
		};
		HttpHeaders headers1 = new HttpHeaders();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			headers1.set(entry.getKey(),entry.getValue());
		}
		HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity<>(headers1);
		String url1 ="http://buff.163.com/api/market/goods?game=csgo&page_num=1&use_suggestion=0&_=1684057330094";
		RestTemplate restTemplate = run.getBean(RestTemplate.class);
		ResponseEntity<ProductList> responseEntity = restTemplate.exchange(url1, HttpMethod.GET, entity1, ProductList.class);
		System.out.println("123123");

	}
}
