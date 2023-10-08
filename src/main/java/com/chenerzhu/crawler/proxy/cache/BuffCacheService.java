package com.chenerzhu.crawler.proxy.cache;


import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.util.bufflogin.BuffAutoLoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


/**
 * buff 配置的持久化配置
 */
@Service
@Slf4j
public class BuffCacheService {

    @Autowired
    BuffAutoLoginUtil buffAutoLoginUtil;


    @Cacheable(value = "buff_cookie", key = "#account")
    public String getCookie(String account, BuffUserData buffUserData) {
        int count = 0;
        String cookie = "";
        while (count++ < 6 && ("null".equals(cookie) || StrUtil.isEmpty(cookie))) {
            cookie = BuffAutoLoginUtil.login(buffUserData.getAcount(), buffUserData.getPwd());
            buffUserData.setCookie(cookie);
            if ("null".equals(cookie) || StrUtil.isEmpty(cookie)) {
                continue;
            }
        }
        return addCookie(account, cookie);
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
