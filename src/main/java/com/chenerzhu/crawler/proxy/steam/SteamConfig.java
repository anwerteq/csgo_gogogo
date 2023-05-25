package com.chenerzhu.crawler.proxy.steam;

import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * steam需要的配置类
 */
public class SteamConfig {

//    public static String STEAM_COOKIE = "timezoneOffset=28800,0; browserid=2911058493333424353; sessionid=a58d18e14bb51b34d6e20ce2; webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22new_device_cooldown_days%22%3A0%2C%22time_checked%22%3A1684894590%7D; steamCountry=US%7Ce8c299cabdd4add970385a84579d8c05; steamLoginSecure=76561199503276197%7C%7CeyAidHlwIjogIkpXVCIsICJhbGciOiAiRWREU0EiIH0.eyAiaXNzIjogInI6MEQyRl8yMjhEQTdDRV9GQ0M4NCIsICJzdWIiOiAiNzY1NjExOTkzNTExODU0MDEiLCAiYXVkIjogWyAid2ViIiBdLCAiZXhwIjogMTY4NDk5MDU3OCwgIm5iZiI6IDE2NzYyNjM4MzksICJpYXQiOiAxNjg0OTAzODM5LCAianRpIjogIjBEMjVfMjI5NkUxOEFfQUQwMDkiLCAib2F0IjogMTY4NDcyNTAwOSwgInJ0X2V4cCI6IDE3MDI5MjkwMjEsICJwZXIiOiAwLCAiaXBfc3ViamVjdCI6ICIzLjEuODUuMjA4IiwgImlwX2NvbmZpcm1lciI6ICIzLjEuODUuMjA4IiB9.Yh5l-OqmuGm95z0UGkkiijb0hLJij5Cmmh494NfdBwvqXEyPJbZjaWuz-TBf6H1aSIiUY1blVAWiGea4zAJQAg; strInventoryLastContext=730_2";
    public static String STEAM_COOKIE = "timezoneOffset=28800,0; _ga=GA1.2.530863508.1684808861; sessionid=919ffea85325aec70a73f3c2; steamCountry=US%7Ce8c299cabdd4add970385a84579d8c05; _gid=GA1.2.1037297630.1685005404; steamLoginSecure=76561199503276197%7C%7CeyAidHlwIjogIkpXVCIsICJhbGciOiAiRWREU0EiIH0.eyAiaXNzIjogInI6MEQyOF8yMjk2RTFERV8wNzVFNiIsICJzdWIiOiAiNzY1NjExOTk1MDMyNzYxOTciLCAiYXVkIjogWyAid2ViIiBdLCAiZXhwIjogMTY4NTA5MjAxNywgIm5iZiI6IDE2NzYzNjU0MjksICJpYXQiOiAxNjg1MDA1NDI5LCAianRpIjogIjBEMzBfMjI5NkUxREVfMDI3OTMiLCAib2F0IjogMTY4NTAwNTQyOSwgInJ0X2V4cCI6IDE3MDI5ODUzMDMsICJwZXIiOiAwLCAiaXBfc3ViamVjdCI6ICI0NS4xNDQuMTM2LjE3MyIsICJpcF9jb25maXJtZXIiOiAiNDUuMTQ0LjEzNi4xNzMiIH0.DpOUVJeT1pFAwa-xci2bp0da7kM0pHi_3YtHbV_Sp50PQVeNdaj_LT7FBEE0aJQ2jFxjLwMGelei7_aAv1cFDQ; browserid=2699390579523579056; webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22new_device_cooldown_days%22%3A0%2C%22time_checked%22%3A1685005444%7D";


    /**
     * steam请求头
     *
     * @return
     */
    public static Map<String, String> getSteamHeader() {
        Map<String, String> headers1 = new HashMap() {{
            //steam请求头
            put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            put("Cookie", SteamConfig.STEAM_COOKIE);
            put("Host", "steamcommunity.com");
            put("Referer", "https://steamcommunity.com/profiles/76561199503276197/inventory?modal=1&market=1");
        }};
        SleepUtil.sleep(550);
        return headers1;
    }


    /**
     * 获取上架需要的请求头
     */
    public static Map<String, String> getSaleHeader() {
        Map<String, String> steamHeader = getSteamHeader();
        steamHeader.put("Referer", "https://steamcommunity.com/profiles/76561199503276197/inventory?modal=1&market=1");
        steamHeader.put("Content-Type", "application/x-www-form-urlencoded");
        return steamHeader;
    }

    /**
     * 获取购买订单需要的请求头
     */
    public static Map<String, String> getBuyHeader() {
        Map<String, String> steamHeader = getSteamHeader();
        steamHeader.put("Content-Type", "application/x-www-form-urlencoded");
        return steamHeader;
    }

}


