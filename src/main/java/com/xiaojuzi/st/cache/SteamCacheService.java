package com.xiaojuzi.st.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * steam配置的持久化配置
 */
@Service
public class SteamCacheService {


    @CachePut(value = "steam_cookie", key = "#account")
    public StringBuilder addCookie(String account, StringBuilder cookieSb) {
        // 将对象存储到缓存中
        return cookieSb;
    }

    @CacheEvict(value = "steam_cookie", key = "#account")
    public void removeCookie(String account) {
        // 从缓存中移除指定的键对应的缓存项
    }


    @Cacheable(value = "steam_apikey", key = "#account")
    public String getApikey(String account) {
        // 从数据库或其他数据源获取数据的逻辑
        // ...

        // 返回获取的数据
        return "";
    }

    @CachePut(value = "steam_apikey", key = "#account")
    public String addApikey(String account, String apikey) {
        // 将对象存储到缓存中
        return apikey;
    }
}
