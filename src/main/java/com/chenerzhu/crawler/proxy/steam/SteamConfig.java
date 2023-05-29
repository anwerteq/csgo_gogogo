package com.chenerzhu.crawler.proxy.steam;

import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * steam需要的配置类
 */
public class SteamConfig {

    public static String STEAM_COOKIE = "ActListPageSize=100; timezoneOffset=28800,0; browserid=2911058493333424353; strInventoryLastContext=730_2; sessionid=192218e8804eb92be4532413; webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22new_device_cooldown_days%22%3A0%2C%22time_checked%22%3A1685008057%7D; steamCountry=CN%7C3b26eb19cc5b4fce969e021b8f74192c; steamLoginSecure=76561199503276197%7C%7CeyAidHlwIjogIkpXVCIsICJhbGciOiAiRWREU0EiIH0.eyAiaXNzIjogInI6MEQzMF8yMjk2RTFERl9CRDIxNyIsICJzdWIiOiAiNzY1NjExOTk1MDMyNzYxOTciLCAiYXVkIjogWyAid2ViIiBdLCAiZXhwIjogMTY4NTQxNDY3OSwgIm5iZiI6IDE2NzY2ODY4MDAsICJpYXQiOiAxNjg1MzI2ODAwLCAianRpIjogIjBEMzBfMjI5NkUyQzFfRUU4MUYiLCAib2F0IjogMTY4NTAwODA1NSwgInJ0X2V4cCI6IDE3MDMyODg2MzUsICJwZXIiOiAwLCAiaXBfc3ViamVjdCI6ICI0NS4xNDQuMTM2LjE3MyIsICJpcF9jb25maXJtZXIiOiAiNDUuMTQ0LjEzNi4xNzMiIH0.E-MtSXUNfvDXlhsHoOTexMPjV_QSPLla77GPHM8KewYKSYDYO9tNa5duC89Za7doTcWV9mc5B7j3w6gHE0n0Cw";
//    public static String STEAM_COOKIE = "ActListPageSize=100; sessionid=738dc9f7afd74bef14c8ad21; timezoneOffset=28800,0; _ga=GA1.2.1844427038.1683893075; browserid=2790587293957642904; _gid=GA1.2.405450854.1684580346; Steam_Language=schinese; steamCountry=US%7Ce8c299cabdd4add970385a84579d8c05; strInventoryLastContext=730_2; webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22new_device_cooldown_days%22%3A0%2C%22time_checked%22%3A1685014444%7D; steamLoginSecure=76561199503276197%7C%7CeyAidHlwIjogIkpXVCIsICJhbGciOiAiRWREU0EiIH0.eyAiaXNzIjogInI6MEQyN18yMjk2RTFEQl84MDZFRCIsICJzdWIiOiAiNzY1NjExOTk1MDMyNzYxOTciLCAiYXVkIjogWyAid2ViIiBdLCAiZXhwIjogMTY4NTI3Njk3OSwgIm5iZiI6IDE2NzY1NDk0MjksICJpYXQiOiAxNjg1MTg5NDI5LCAianRpIjogIjBEMzBfMjI5NkUyNUZfRjM2Q0IiLCAib2F0IjogMTY4NTAxNDQyNywgInJ0X2V4cCI6IDE3MDMyNjY1MTEsICJwZXIiOiAwLCAiaXBfc3ViamVjdCI6ICI0NS4xNDQuMTM2LjE3MyIsICJpcF9jb25maXJtZXIiOiAiNDUuMTQ0LjEzNi4xNzMiIH0.GgXnoakqJcoJxPx9vpBr18Hhgl5jbrat3fj8wxRbFSu4CQpK2Xoi6kWnEN5F1_mNF9rMNfGPDjOpD3MIdHz1BQ; tsTradeOffersLastRead=1685189589";


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
        steamHeader.put("Content-Type", "application/x-www-form-urlencoded");
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

        return STEAM_COOKIE;
    }

    public static  String getCookieOnlyKey(String key){
        String[] split = getCookie().split(key + "=");
        String value = split[1].split(";")[0];
        return value.trim();
    }
}


