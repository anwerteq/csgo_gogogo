package com.chenerzhu.crawler.proxy.applicationRunners;


import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.buff.service.PullItemService;
import com.chenerzhu.crawler.proxy.cache.SteamCacheService;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.steamlogin.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 启动后steam账号信息初始化
 */
@Slf4j
@Component
@Order(1)
public class SteamApplicationRunner implements ApplicationRunner {

    public static List<SteamUserDate> steamUserDates = new ArrayList<>();

    public static ThreadLocal<SteamUserDate> steamUserDateTL = new ThreadLocal<>();


    /**
     * 通过steamid设置steam信息
     *
     * @param steamId
     */
    public static boolean setThreadLocalSteamId(String steamId) {
        for (SteamUserDate steamUserDate : steamUserDates) {
            Session session = steamUserDate.getSession();
            if (steamId.equals(session.getSteamID())) {
                steamUserDateTL.set(steamUserDate);
                CookiesConfig.steamCookies.set(steamUserDate.getCookies().toString());
                return true;
            }
        }
        log.error("该系统未加载steamId:{}账号信息，请检查sda路径", steamId);
        log.info("随机设置一个steam账号进行操作");
        int index = Math.toIntExact(System.currentTimeMillis() % steamUserDates.size());
        CookiesConfig.steamCookies.set(steamUserDates.get(index).getCookies().toString());
        return false;
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
        if (steamUserDatesInit.isEmpty()) {
            log.error("为找到有效sda文件，请检查sda路径(sda路径不能包含中文)，正在关闭脚本");
            SleepUtil.sleep(5000);
            System.exit(1);
        }
        log.info("开始登录steam账号");
        SteamUserDate steamUserDate1 = steamUserDatesInit.get(0);
        steamUserDates.add(loginSteamUserDate(steamUserDate1));

        PullItemService.executorService.execute(() -> {
            for (int i = 1; i < steamUserDatesInit.size(); i++) {
                SteamUserDate steamUserDate = steamUserDatesInit.get(i);
                while (true) {
                    try {
                        SleepUtil.sleep(60 * 1000);
                        steamUserDates.add(loginSteamUserDate(steamUserDate));
                    } catch (Exception e) {
                        log.info("steam账号，steamId:{}登录失败，睡眠后继续登录", steamUserDate.getAccount_name());
                        SleepUtil.sleep(60 * 1000);
                    }
                }
            }
        });

    }

    public SteamUserDate loginSteamUserDate(SteamUserDate steamDate) {
        String account_name = steamDate.getAccount_name();
        //从缓存中获取cookie
        StringBuilder cookieSb = steamCacheService.getCookie(account_name);
        if (StrUtil.isEmpty(cookieSb.toString())) {
            int count = 0;
            while (count++ < 5) {
                try {
                    cookieSb = steamLoginUtil.login(steamDate);
                } catch (Exception e) {
                    log.info("steam账号:{}，第：{}次尝试失败,睡眠10s,进行下一次尝试", steamDate.getAccount_name(), count);
                    SleepUtil.sleep(60000);
                }
                if (StrUtil.isNotEmpty(cookieSb.toString())) {
                    break;
                }
            }
            if (count >= 3) {
                log.info("steam账号:{}，尝试{}次后，还是登录失败,请切换clash节点", steamDate.getAccount_name(), count);
                SleepUtil.sleep(10000);
            }
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
        log.info("成功加载steam账号，steamId:{}", steamDate.getSession().getSteamID());
        return steamDate;
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
        if (!response.contains("Key: ") && !response.contains("密钥: ")) {
            log.error("{}:获取交易apikye失败，请访问[ https://steamcommunity.com/dev/apikey ] 检查是否有 apikey链接", account);
            System.exit(0);
        }
        String[] arr = response.split("Key: ");
        if (arr.length == 1) {
            arr = response.split("密钥: ");
        }
        String apikey = arr[1].split("</p>")[0];
        log.info("{}:获取交易apikye为{}", account, apikey);
        return apikey;
    }
}
