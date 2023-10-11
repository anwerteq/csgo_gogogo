package com.chenerzhu.crawler.proxy.cache;


import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.util.bufflogin.BuffAutoLoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;


/**
 * buff 配置的持久化配置
 */
@Service
@Slf4j
public class BuffCacheService {

    @Autowired
    BuffAutoLoginUtil buffAutoLoginUtil;

    @Autowired
    RestTemplate restTemplate;


    @Cacheable(value = "buff_cookie", key = "#account")
    public String getCookie(String account, BuffUserData buffUserData) {
        int count = 0;
        String cookie = "";
        while (count++ < 6 && ("null".equals(cookie) || StrUtil.isEmpty(cookie))) {
            cookie = BuffAutoLoginUtil.login(buffUserData.getAcount(), buffUserData.getPwd());

            if ("null".equals(cookie) || StrUtil.isEmpty(cookie)) {
                continue;
            }
        }
        cookie = getCookieDetail(cookie);
        buffUserData.setCookie(cookie);
        return addCookie(account, cookie);
    }

    /**
     * 获取cookie全的明细信息
     *
     * @return
     */
    public String getCookieDetail(String cookie) {
        String url = "https://buff.163.com/api/market/sell_order/on_sale?page_num=1&sort_by=updated.asc" +
                "&mode=2%2C5&game=csgo&appid=730&page_size=30&min_price=" + 9999;
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) \"\n" +
                "                      \"Chrome/105.0.0.0 Safari/537.36 Edg/105.0.1343.27");

//        String url = "https://buff.163.com/api/market/goods?game=csgo&page_num=1&use_suggestion=0&_=1696863430757&page_size=40";
        CookiesConfig.buffCookies.set(cookie);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        List<String> cookies = responseEntity.getHeaders().get("set-cookie");
        StringJoiner sj = new StringJoiner(";");
        for (String value : cookies) {
            sj.add(value);
        }
        String cookieDetail = sj.toString();
        return cookieDetail;
    }

    @CachePut(value = "buff_cookie", key = "#account")
    public String addCookie(String account, String cookie) {
        // 将对象存储到缓存中
        return cookie;
    }

    @CacheEvict(value = "buff_cookie", key = "#account")
    public void removeCookie(String account) {
        // 从缓存中移除指定的键对应的缓存项
    }


    @Cacheable(value = "buff_steamId", key = "#account")
    public String getSteamId(String account, String cookie) {
        if ("null".equals(cookie) || StrUtil.isEmpty(cookie)) {
            log.info("buff账号：{}，获取cookie失败");
        }
        int count = 0;
        String steamId = "";
        while (count++ < 4 && StrUtil.isEmpty(steamId)) {
            steamId = buffAutoLoginUtil.getSteamId(cookie);
        }
        return addSteamId(account, steamId);
    }

    @CachePut(value = "buff_steamId", key = "#account")
    public String addSteamId(String account, String steamId) {
        // 将对象存储到缓存中
        return steamId;
    }

    @CacheEvict(value = "buff_steamId", key = "#account")
    public void removeSteamId(String account) {
        // 从缓存中移除指定的键对应的缓存项
    }
}
