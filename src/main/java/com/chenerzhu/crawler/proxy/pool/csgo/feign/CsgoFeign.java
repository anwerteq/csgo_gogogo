package com.chenerzhu.crawler.proxy.pool.csgo.feign;


import com.chenerzhu.crawler.proxy.pool.csgo.entity.ProductList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "service-csgo",url = "https://buff.163.com")
public interface CsgoFeign {

    @GetMapping("/api/market/goods?game=csgo&use_suggestion=0&_=1683997415442")
    ProductList getItems(String page_num);
}
