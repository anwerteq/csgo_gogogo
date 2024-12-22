package com.xiaojuzi.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;


@Configuration
public class ApplicationContextConfig {


    @Bean
    private static RestTemplate createRestTemplateWithRetry() {
        // 创建 RetryTemplate
        RetryTemplate retryTemplate = new RetryTemplate();

        // 设置固定延迟重试策略
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(30000); // 20秒
        retryTemplate.setBackOffPolicy(backOffPolicy);

        // 设置简单的重试策略：只对指定异常进行重试，重试最多3次
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3, Collections.singletonMap(IOException.class, true));
        retryTemplate.setRetryPolicy(retryPolicy);

        // 创建 RestTemplate 并添加拦截器
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new RetryInterceptor(retryTemplate)));

        return restTemplate;
    }

    // 自定义拦截器实现
    private static class RetryInterceptor implements ClientHttpRequestInterceptor {
        private final RetryTemplate retryTemplate;

        public RetryInterceptor(RetryTemplate retryTemplate) {
            this.retryTemplate = retryTemplate;
        }

        @Override
        public ClientHttpResponse intercept(
                org.springframework.http.HttpRequest request,
                byte[] body,
                org.springframework.http.client.ClientHttpRequestExecution execution
        ) throws IOException {
            try {
                return retryTemplate.execute(
                        (RetryCallback<ClientHttpResponse, IOException>) context -> {
                            ClientHttpResponse response = execution.execute(request, body);
                            if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                                throw new IOException("HTTP 429 Too Many Requests");
                            }
                            return response;
                        }
                );
            } catch (IOException e) {
                System.err.println("Request failed after retries: " + e.getMessage());
                throw e;
            }
        }
    }
}
