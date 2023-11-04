package com.chenerzhu.crawler.proxy.steam;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.applicationRunners.SteamApplicationRunner;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * steam需要的配置类
 */
@Configuration
@Slf4j
public class SteamConfig implements ApplicationRunner {


    public static String STEAM_COOKIE;

    @Value("${steam_session}")
    public  String STEAM_COOKIE1;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        STEAM_COOKIE=STEAM_COOKIE1;
        log.info("steam加载的cookie数据为："+ STEAM_COOKIE);
    }


    /**
     * steam请求头
     *
     * @return
     */
    public static Map<String, String> getSteamHeader() {
        Map<String, String> headers1 = new HashMap() {{
            //steam请求头
//            put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            put("Cookie", getCookie());
            put("Host", "steamcommunity.com");
//            put("Referer", "https://steamcommunity.com/profiles/"+SteamConfig.getSteamId()+"/inventory?modal=1&market=1");
        }};
        return headers1;
    }


    /**
     * 获取上架需要的请求头
     */
    public static Map<String, String> getSaleHeader() {
        Map<String, String> steamHeader = getSteamHeader();
        steamHeader.put("Referer", "https://steamcommunity.com/profiles/"+SteamConfig.getSteamId()+"/inventory?modal=1&market=1");
        steamHeader.put("Content-Type", "application/x-www-form-urlencoded");
        return steamHeader;
    }

    /**
     * 获取购买订单需要的请求头
     */
    public static Map<String, String> getBuyHeader() {
        Map<String, String> steamHeader = getSteamHeader();
        steamHeader.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        return steamHeader;
    }

    /**
     * 获取steamId
     */
    public static String getSteamId(){
        String steamID = SteamApplicationRunner.steamUserDateTL.get().getSession().getSteamID();
//        String steamLoginSecure = getCookieOnlyKey("steamLoginSecure");
//        String substring = steamLoginSecure.substring(0, 17);
        return steamID;
    }

    public static  String getCookie(){
        String cookies = CookiesConfig.steamCookies.get();
        if (StrUtil.isNotEmpty(cookies)){
            return cookies;
        }
        cookies = SteamApplicationRunner.steamUserDates.get(0).getCookies().toString();
        return cookies;
    }

    public static  String getCookieOnlyKey(String key) {
        String[] split = getCookie().split(key + "=");
        String value = split[1].split(";")[0];
        return value.trim();
//        String sessionID = SteamApplicationRunner.steamUserDateTL.get().getSession().getSessionID();
//        return sessionID;
    }


}


