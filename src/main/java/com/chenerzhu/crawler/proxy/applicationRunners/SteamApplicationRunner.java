package com.chenerzhu.crawler.proxy.applicationRunners;


import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.cache.SteamCacheService;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.util.steamlogin.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 启动后steam账号信息初始化
 */
@Slf4j
@Component
public class SteamApplicationRunner implements ApplicationRunner {

    public static List<SteamUserDate> steamUserDates = new ArrayList<>();

    public static ThreadLocal<SteamUserDate> steamUserDateTL = new ThreadLocal<>();


    /**
     * 通过steamid设置steam信息
     *
     * @param steamId
     */
    public static void setThreadLocalSteamId(String steamId) {
        for (SteamUserDate steamUserDate : steamUserDates) {
            Session session = steamUserDate.getSession();
            if (steamId.equals(session.getSteamID())) {
                steamUserDateTL.set(steamUserDate);
                CookiesConfig.steamCookies.set(steamUserDate.getCookies().toString());
                return;
            }
        }
        log.error("该系统未加载steamId:{}账号信息，请检查sda路径", steamId);
    }

    @Autowired
    Http http;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    SteamCacheService steamCacheService;
    @Value("${sda_path}")
    private String sdaPath;
    @Autowired
    private SteamLoginUtil steamLoginUtil;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<SteamUserDate> steamUserDatesInit = SteamLoginUtil.readFilesInFolder(sdaPath);
        for (SteamUserDate steamDate : steamUserDatesInit) {
            String account_name = steamDate.getAccount_name();
            //从缓存中获取cookie
            StringBuilder cookieSb = steamCacheService.getCookie(account_name);
            if (StrUtil.isEmpty(cookieSb.toString())) {
                cookieSb = steamLoginUtil.login(steamDate);
            }
            //校验cookie是否过期
            if (steamLoginUtil.checkCookieExpired(cookieSb.toString())) {
                steamCacheService.removeCookie(account_name);
            }
            //缓存cookie
            steamCacheService.addCookie(account_name, cookieSb);
            steamDate.setCookies(cookieSb);
            //获取apikey
            String apikey = steamCacheService.getApikey(account_name);
            if (StrUtil.isEmpty(apikey)) {
                apikey = getApikey(account_name, steamDate.getCookies().toString());
                steamCacheService.addApikey(account_name, apikey);
            }
            steamDate.setApikey(apikey);
        }
        steamUserDates.addAll(steamUserDatesInit);
    }


    /**
     * 获取steam的交易连接
     *
     * @param cookie
     * @return
     */
    public String getApikey(String account, String cookie) {
        String url = "https://steamcommunity.com/dev/apikey";
        CookiesConfig.steamCookies.set(cookie);
        HttpBean httpBean = http.request(url,
                "GET", null, cookie, true, "http://steamcommunity.com/id/csgo/tradeoffers/sent/", true);
        String response = httpBean.getResponse();
        if (!response.contains("Key: ")) {
            log.error("{}:获取交易apikye失败，请访问[ https://steamcommunity.com/dev/apikey ] 检查是否有 apikey链接", account);
            System.exit(0);
        }
        String apikey = response.split("Key: ")[1].split("</p>")[0];
        log.info("{}:获取交易apikye为{}", account, apikey);
        return apikey;
    }
}
