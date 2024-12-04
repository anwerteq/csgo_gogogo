package com.xiaojuzi.st.util.steamlogin;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.xiaojuzi.st.steam.util.SleepUtil;
import com.xiaojuzi.st.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.*;


/**
 * steam登录的工具类
 */
@Slf4j
@Component
public class SteamLoginUtil {
    @Autowired
    Http http;




    /**
     * 登录账号
     *
     * @param folderPath
     * @return
     */
    public static List<SteamUserDate> readFilesInFolder(String folderPath) throws Exception {
        List<SteamUserDate> steamUserDates = new ArrayList();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().contains(".maFile")) {
                        steamUserDates.add(readJsonFromFile(file));
                    } else if (file.isDirectory()) {
                        steamUserDates.addAll(readFilesInFolder(file.getAbsolutePath()));
                    }
                }
            }
        }
        return steamUserDates;
    }

    public static SteamUserDate readJsonFromFile(File file) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder content = new StringBuilder();

            while ((line = br.readLine()) != null) {
                content.append(line);
            }

            String jsonString = content.toString();
            SteamUserDate steamUserDate = JSONObject.parseObject(jsonString, SteamUserDate.class);
            new Thread(new SampleWebCookie(steamUserDate.getAccount_name(), "QingLiu98!",steamUserDate)).start();
            return steamUserDate;
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }


    public static String randomHexNumber() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] buffer = new byte[16];
        secureRandom.nextBytes(buffer);
        StringBuilder hexStr = new StringBuilder();
        for (byte b : buffer) {
            hexStr.append(String.format("%02x", b));
        }
        return hexStr.toString();
    }

    /**
     * 获取sessionId
     *
     * @return
     */
    public static String generateSessionID() {
        return randomHexNumber();
    }




    /**
     * 获取cookie信息
     *
     * @param steamUserDate
     * @return
     */
    public StringBuilder login(SteamUserDate steamUserDate) {
        steamUserDate.getSession().setSessionID(generateSessionID());
        String cookies = getCookies(steamUserDate);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cookies);
        return stringBuilder;
    }

    /**
     * 获取accessToken
     *
     * @param steamUserDate
     * @return
     */
    public String get_access_token(SteamUserDate steamUserDate) {
        String url = "https://api.steampowered.com/IAuthenticationService/GenerateAccessTokenForApp/v1/";
        Map<String, String> headerMap = new HashMap() {{
            put("Referer", "https://steamcommunity.com");
        }};
        Map<String, String> dataMap = new HashMap() {{
            put("refresh_token", steamUserDate.getSession().getRefreshToken());
            put("steamid", steamUserDate.getSession().getSteamID());

        }};
        String response = HttpClientUtils.sendPostForm(url, "", headerMap, dataMap);
        JSONObject jsonObject = JSONObject.parseObject(response);
        String access_token = jsonObject.getJSONObject("response").getString("access_token");
        if (StrUtil.isEmpty(access_token)){
            log.error("获取access_token失败");
            System.exit(0);
        }
        access_token = steamUserDate.getSession().getSteamID() + "%7C%7C" + access_token;
        return access_token;
    }

    public String getCookies(SteamUserDate steamUserDate) {
        HashMap<Object, Object> map = new HashMap() ;
        map.put("steamLoginSecure", SteamUserDate.steamTokensNumberAndTokenMap.get(steamUserDate.getAccount_name()));
        map.put("sessionid", steamUserDate.getSession().getSessionID());
        map.put("Steam_Language", "english");
        map.put("timezoneOffset", "28800,0");
        map.put("_ga", "GA1.2.234547838.1688523763");
        map.put("browserid", "'2685889387687629642'");
        map.put("strInventoryLastContext", "2504460_2");
        StringJoiner sj = new StringJoiner(";");
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            sj.add(entry.getKey() + "=" + entry.getValue());
        }
        return sj.toString();
    }

    /**
     * 校验cookie是否过期
     *
     * @param cookie
     * @return true:是，false:否
     */
    public Boolean checkCookieExpired(String cookie) {
        log.info("steam的cookie开始测试 代理ip: {}", cookie);

        try {
            String url = "https://steamcommunity.com/market/priceoverview/?country=US&currency=1&appid=730&market_hash_name="
                    + URLEncoder.encode("Sticker | Mahjong Zhong", "UTF-8");
            HttpBean httpBean = http.request(url,
                    "GET", null, cookie, true, "http://steamcommunity.com/id/csgo/tradeoffers/sent/", true);
            log.info("代理IP访问steam,测试接口返回的数据为：{}", httpBean.getResponse());
            if (httpBean.getResponse().contains("true")) {
                return false;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error("通过代理ip访问steam失败，请检查 proxyIp 的配置，正在关闭程序");
            SleepUtil.sleep(5000);
            System.exit(1);
        }
        return true;
    }


}
