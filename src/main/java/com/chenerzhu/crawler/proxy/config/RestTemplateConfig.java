package com.chenerzhu.crawler.proxy.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // 配置 StringHttpMessageConverter
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        restTemplate.getMessageConverters().add(0, stringConverter);
        return restTemplate;
    }
}

// 自定义拦截器
class CustomInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        // 可以修改请求头、请求体等
        // 执行请求
        ClientHttpResponse response = execution.execute(request, body);
// 获取响应头中的 Set-Cookie 字段
        HttpHeaders headers = response.getHeaders();
        List<String> setCookieHeaders = headers.get(HttpHeaders.SET_COOKIE);

        // 将 Set-Cookie 设置到自己的 Cookie 中
        if (setCookieHeaders != null) {
            List<String> list = request.getHeaders().get("Cookie");
            String cookies = list.get(0);
            Map<String, Object> headerMap = new HashMap();
            String[] split = cookies.split(";");
            for (String str : split) {
                String[] split1 = str.split("=");
                String value = split1.length == 2 ? split1[1] : "";
                headerMap.put(split1[0], value);
            }
            for (String header : setCookieHeaders) {
                String[] split1 = header.split("=");
                String key = split1[0];
                String value = split1.length >= 2 ? header.replaceFirst(key + "=", "") : "";
                headerMap.put(key, value);
            }
            StringJoiner sj = new StringJoiner(";");
            for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                sj.add(entry.getKey() + "=" + entry.getValue());
            }
            CookiesConfig.buffCookies.set(sj.toString());
        }
        // 可以修改响应内容等
        return response;
    }
}
