package com.chenerzhu.crawler.proxy.steam;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.entity.Cookeis;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
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
public class SteamConfig implements ApplicationRunner {

//    @Value("${steam_session}")
    public static String STEAM_COOKIE ="browserid=2756820353045594770; timezoneOffset=28800,0; strInventoryLastContext=730_2; sessionid=6037b3992d333d4584da4bfc; webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22new_device_cooldown_days%22%3A0%2C%22time_checked%22%3A1694012255%7D; steamCountry=CN%7Cbce92f89b560fb52ef004658776ef747; steamLoginSecure=76561199503276197%7C%7CeyAidHlwIjogIkpXVCIsICJhbGciOiAiRWREU0EiIH0.eyAiaXNzIjogInI6MEQxOF8yMzE4MTQyQl9FNUQ1MSIsICJzdWIiOiAiNzY1NjExOTk1MDMyNzYxOTciLCAiYXVkIjogWyAid2ViIiBdLCAiZXhwIjogMTY5NDI3NjA2NCwgIm5iZiI6IDE2ODU1NDkwMjcsICJpYXQiOiAxNjk0MTg5MDI3LCAianRpIjogIjBEMjZfMjMyMkFFQ0RfQ0Q1QTIiLCAib2F0IjogMTY5MzYyODk3MywgInJ0X2V4cCI6IDE3MTE5Njc2ODksICJwZXIiOiAwLCAiaXBfc3ViamVjdCI6ICIxMzkuMjI3LjEzLjE1IiwgImlwX2NvbmZpcm1lciI6ICIxMzkuMjI3LjEzLjE1IiB9.iJrf6_vJy2nzjEgsGEmsBBf2cTlI2ECYwg0zusVbHTqsjJV7xVy7TbXg0ZqRVRA-p8DS1w7ib4aVSSaytV62BA" ;


    @Override
    public void run(ApplicationArguments args) throws Exception {

    }


    /**
     * steam请求头
     *
     * @return
     */
    public static Map<String, String> getSteamHeader() {
        Map<String, String> headers1 = new HashMap() {{
            //steam请求头
            put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            put("Cookie", getCookie());
            put("Host", "steamcommunity.com");
            put("Referer", "https://steamcommunity.com/profiles/"+SteamConfig.getSteamId()+"/inventory?modal=1&market=1");
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
        String steamLoginSecure = getCookieOnlyKey("steamLoginSecure");
        String substring = steamLoginSecure.substring(0, 17);
        return substring;
    }

    public static  String getCookie(){
        String cookies = STEAM_COOKIE;
        if (StrUtil.isNotEmpty(cookies)){
            return cookies;
        }
        long millis = System.currentTimeMillis();
        int size = CookiesConfig.cookeisList.size();
        int index = (int) (millis%size);
        Cookeis cookeis = CookiesConfig.cookeisList.get(index);
        CookiesConfig.steamCookies.set(cookeis.getSteam_cookie());
        return cookeis.getSteam_cookie();
    }

    public static  String getCookieOnlyKey(String key){
        String[] split = getCookie().split(key + "=");
        String value = split[1].split(";")[0];
        return value.trim();
    }


}


