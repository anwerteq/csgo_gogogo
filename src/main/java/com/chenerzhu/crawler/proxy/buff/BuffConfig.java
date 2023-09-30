package com.chenerzhu.crawler.proxy.buff;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.entity.Cookeis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * buff配置类
 */
@Configuration
@Slf4j
public class BuffConfig implements ApplicationRunner {


    @Value("${buff_session}")
   public  String buffCookie = "";

    public static String buffCookieStatic;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        buffCookieStatic = buffCookie ;
        log.info("buff 加载的cookie数据为："+ buffCookieStatic);
    }



    public  static Map<String, String> map = new HashMap() {
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
        }
    };


    public static HttpEntity<MultiValueMap<String, String>> getBuffCreateBillHttpEntity() {
        HttpHeaders headers1 =getHeaderMap();
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity(headers1);
        return entity1;
    }


    public static HttpEntity<MultiValueMap<String, String>> getBuffHttpEntity() {
        HttpHeaders headers1 = getHeaderMap();
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity(headers1);
        syncCookie();
        return entity1;
    }

    /**
     * 获取请求头参数
     * @return
     */
    public static HttpHeaders getHeaderMap(){
        HttpHeaders headers1 = new HttpHeaders();
        Map<String, String> headerMap = map;
        headerMap.put("Cookie",getCookie());
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            headers1.set(entry.getKey(), entry.getValue());
        }
        return headers1;
    }

    public static HttpEntity<MultiValueMap<String, String>> getBuffHttpEntity(Map<String, Object> whereMap) {
        HttpHeaders headers1 = getHeaderMap();
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity(whereMap, headers1);
        syncCookie();
        return entity1;
    }

    /**
     * 控制整个项目使用cookie的频率
     */
    public static void syncCookie() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getCookie(){
        if(true){
            return buffCookieStatic;
        }
        String cookie = CookiesConfig.buffCookies.get();
        if (StrUtil.isNotEmpty(cookie)){
            return cookie;
        }
        long millis = System.currentTimeMillis();
        int size = CookiesConfig.cookeisList.size();
        //线程没有绑定cooke,随机获取一个cookie
        int index = (int) (millis % size);
        Cookeis cookeis = CookiesConfig.cookeisList.get(index);
        CookiesConfig.buffCookies.set(cookeis.getBuff_cookie());
        return cookeis.getBuff_cookie();
    }

    /**
     * 获取cookie中的value属性
     * @param key
     * @return
     */
    public static  String getCookieOnlyKey(String key){
        String[] split = getCookie().split(key + "=");
        String value = split[1].split(";")[0];
        return value.trim();
    }

}
