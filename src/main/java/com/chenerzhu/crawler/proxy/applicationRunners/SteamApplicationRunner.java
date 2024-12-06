package com.chenerzhu.crawler.proxy.applicationRunners;


import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.cache.SteamCacheService;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.SteamTheadeUtil;
import com.chenerzhu.crawler.proxy.util.steamlogin.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chenerzhu.crawler.proxy.util.SteamTheadeUtil.steamUserDates;

/**
 * 启动后steam账号信息初始化
 */
@Slf4j
@Component
@Order(4)
public class SteamApplicationRunner implements ApplicationRunner {




    /**
     * 通过steamid设置steam信息
     *
     * @param steamId
     */
    public static boolean setThreadLocalSteamId(String steamId) {
        for (SteamUserDate steamUserDate : steamUserDates) {
            Session session = steamUserDate.getSession();
            if (steamId.equals(session.getSteamID())) {
                SteamTheadeUtil.steamUserDateTL.set(steamUserDate);
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

    /**
     * 校验是否有 steam cookie
     *
     * @return
     */
    public static Boolean checkHasSteamCookie() {
        String theadLocalCookie = SteamConfig.getTheadLocalCookie();
        return (Boolean) StrUtil.isNotEmpty(theadLocalCookie);
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
        List<SteamUserDate> steamUserDatesInit = SteamLoginUtil.readFilesInFolder(new String(sdaPath.getBytes(StandardCharsets.ISO_8859_1)));
        if (steamUserDatesInit.isEmpty()) {
            log.error("为找到有效sda文件，请检查sda路径(sda路径不能包含中文)，正在关闭脚本");

            SleepUtil.sleep(5000);

            return;
//            System.exit(1);
        }
        log.info("开始登录steam账号");
        for (int i = 0; i < steamUserDatesInit.size(); i++) {
            SteamUserDate steamUserDate = steamUserDatesInit.get(i);
            while (true) {
                try {
                    steamUserDates.add(loginSteamUserDate(steamUserDate));
                } catch (Exception e) {
                    log.info("steam账号，steamId:{}登录失败，睡眠后继续登录", steamUserDate.getAccount_name());
                    SleepUtil.sleep(60 * 1000);
                    continue;
                }
                break;
            }
            SleepUtil.sleep(4 * 1000);
        }
    }

    public SteamUserDate loginSteamUserDate(SteamUserDate steamDate) {
        String account_name = steamDate.getAccount_name();
        //从缓存中获取cookie
        StringBuilder cookieSb = new StringBuilder();
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

        steamDate.setCookies(cookieSb);
        //获取apikey
        SleepUtil.sleep(2 * 1000);
        String apikey = steamCacheService.getApikey(account_name);
        if (StrUtil.isEmpty(apikey)) {
            apikey = getApikey(account_name, steamDate);
            steamCacheService.addApikey(account_name, apikey);
        }
        steamDate.setApikey(apikey);
        log.info("成功加载steam账号，steamId:{}", steamDate.getSession().getSteamID());
        return steamDate;
    }


    /**
     * 获取steam的交易连接
     *
     * @param steamDate
     * @return
     */
    public String getApikey(String account, SteamUserDate steamDate) {
        String cookie = steamDate.getCookies().toString();
        String url = "https://steamcommunity.com/dev/apikey";
        CookiesConfig.steamCookies.set(cookie);
        HttpBean httpBean = http.request(url,
                "GET", null, cookie, true, "http://steamcommunity.com/id/csgo/tradeoffers/sent/", true);
        String response = httpBean.getResponse();
        // 定义正则表达式匹配 API 密钥（32 个十六进制字符）
        String apiKeyRegex = "\\b[0-9A-F]{32}\\b";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(apiKeyRegex);
        Matcher matcher = pattern.matcher(response);
        String apikey = "";
        // 查找并输出匹配的 API 密钥
        while (matcher.find()) {
             apikey = matcher.group();
        }
        if (StrUtil.isEmpty(apikey)) {
            log.error("{}:获取交易apikye失败，请访问[ https://steamcommunity.com/dev/apikey ] 检查是否有 apikey链接", account);
        }

        log.info("{}:获取交易apikye为: {}", account, apikey);
        return apikey;
    }
}
