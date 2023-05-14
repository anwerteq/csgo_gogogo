package com.chenerzhu.crawler.proxy.pool.csgo.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {
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
            put("Cookie", "Device-Id=8qNWoqIr0y4cISb0LBIS; client_id=YlNlv5mS1lJdkqcidGXcwQ; csrf_token=ImJmYzRjNDFjMmMyNzdhMGM4YTA1YzZmMDQ4MDAxMGMyODE1YjhlMWYi.F0I_8A.tXdgSHXaD-pfz5yCe5myWqrAgz4");
        }
    };

    @Override
    public void apply(RequestTemplate template) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            template.header(entry.getKey(),entry.getValue());
        }
    }

}
