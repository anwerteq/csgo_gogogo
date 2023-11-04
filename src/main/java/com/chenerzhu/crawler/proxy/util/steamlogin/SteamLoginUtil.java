package com.chenerzhu.crawler.proxy.util.steamlogin;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;
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


    public static void main(String[] args) throws UnsupportedEncodingException {
        String url = "https://steamcommunity.com/market/priceoverview/?country=US&currency=1&appid=730&market_hash_name="
                + URLEncoder.encode("Sticker | Mahjong Zhong", "UTF-8");
        System.out.println(url);
    }

    /**
     * 登录账号
     *
     * @param folderPath
     * @return
     */
    public static List<SteamUserDate> readFilesInFolder(String folderPath) {
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

    public static SteamUserDate readJsonFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder content = new StringBuilder();

            while ((line = br.readLine()) != null) {
                content.append(line);
            }

            String jsonString = content.toString();
            SteamUserDate steamUserDate = JSONObject.parseObject(jsonString, SteamUserDate.class);
//            String password = file.getName().split("-")[1].split("\\.")[0];
//            steamUserDate.setUserPsw(password);
//            login(steamUserDate);
            // 在这里对 jsonObject 进行你需要的处理
//            System.out.println(JSONObject.parseObject(jsonString));
            return steamUserDate;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
    public StringBuilder login1(SteamUserDate steamUserDate) {
        String userName = steamUserDate.getAccount_name();
        Http Http = new Http();
        StringBuilder cookies = new StringBuilder();

        //需要发送的数据 need-send data
        Map<String, String> data = new HashMap<>();
        try {

            //获取sessionId
            HttpBean res = Http.request("https://steamcommunity.com", "GET", data, null, true,
                    null, false);
            if (null != res.getCookies()) {
                cookies.append(res.getCookies());
                steamUserDate.getSession().setSessionID(res.getSession());
            }

            data.put("username", userName);
            data.put("donotcache", Long.toString(TimeUtil.getTimeStamp() * 1000L));
//            data.put("donotcache", Long.toString(System.currentTimeMillis()));
            final String rsaResponse1;
            res = Http.request("https://steamcommunity.com/login/getrsakey", "POST", data, cookies.toString(), true,
                    "https://steamcommunity.com/login/home/?goto=", false);
            rsaResponse1 = String.valueOf(res.getResponse());
            if (null != res.getCookies()) {
                cookies.append(res.getCookies());
            }
            RsaKey rsaKey = JSONObject.parseObject(rsaResponse1, RsaKey.class);

            // 第一次验证 first validate
            if (null == rsaKey || !rsaKey.isSuccess()) {
                log.error("获取Steam的ras_key失败，可能是代理ip访问steam过多，请切换节点 get rsa_key1 error:{}，正在退出脚本", rsaResponse1);
                SleepUtil.sleep(5000);
                System.exit(1);
            }
            // 第一次获取Rsa公钥 first get key
            RSA rsa = new RSA(rsaKey.getPublickey_mod(), rsaKey.getPublickey_exp());
            //公钥加密 lock
            final String password64 = rsa.encrypt(steamUserDate.getUserPsw());

            log.info("登录中...");

            String time = URLEncoder.encode(rsaKey.getTimestamp(), "UTF-8");

            data.clear();
            data.put("password", password64);
            data.put("username", userName);
            data.put("emailauth", "");
            data.put("emailsteamid", "");
            data.put("twofactorcode", "");
            data.put("rsatimestamp", time);
            data.put("remember_login", "true");
            data.put("donotcache", Long.toString(TimeUtil.getTimeStamp() * 1000L));
            SleepUtil.sleep(700);
            res = Http.request("https://steamcommunity.com/login/dologin/", "POST", data, cookies.toString(), true,
                    "https://steamcommunity.com/login/home/?goto=", false);
            if (429 == res.getCode()) {
                log.error("因访问steam太频繁，[" + steamUserDate.getAccount_name() + "]尝试重新登录");
                SleepUtil.sleep(3000);
            }

            final String loginResponse1 = String.valueOf(res.getResponse());

            DoLoginResultBean doLoginResult1 = JSONObject.parseObject(loginResponse1, DoLoginResultBean.class);

            if (!doLoginResult1.isRequires_twofactor()) {
                log.error("steam账号登录失败，失败原因是该ip访问steam次数太多，需要点进图片验证才能登陆，" +
                        "本软件目前不支持。临时解决方法：更换访问steam的ip，或者过一会(10分钟)试试");
                throw new Exception(doLoginResult1.getMessage());
            }
            data.clear();
            data.put("username", userName);
            data.put("donotcache", Long.toString(TimeUtil.getTimeStamp() * 1000L));
            res = Http.request("https://steamcommunity.com/login/getrsakey", "POST", data, cookies.toString(), true,
                    "https://steamcommunity.com/login/home/?goto=", false);
            String rsaResponse2 = String.valueOf(res.getResponse());

            if (null != res.getCookies()) {
                cookies.append(res.getCookies());
            }

            rsaKey = JSONObject.parseObject(rsaResponse2, RsaKey.class);
            if (null == rsaKey || !rsaKey.isSuccess()) {
                throw new LoginException("get rsa_key2 error");
            }
            log.info("验证码自动输入中...");
            data = new HashMap<>();
            // 第二次获取Rsa公钥 second get key
            rsa = new RSA(rsaKey.getPublickey_mod(), rsaKey.getPublickey_exp());
            time = URLEncoder.encode(rsaKey.getTimestamp(), "UTF-8");

            final String password64_2 = rsa.encrypt(steamUserDate.getUserPsw());
            data.clear();
            data.put("password", password64_2);
            data.put("username", userName);
            data.put("emailauth", "");
            data.put("emailsteamid", "");
            data.put("rsatimestamp", time);
            data.put("twofactorcode", ConfirmUtil.getGuard(steamUserDate.getShared_secret()));
            data.put("remember_login", "false");
            data.put("donotcache", Long.toString(TimeUtil.getTimeStamp() * 1000L));
            res = Http.request("https://steamcommunity.com/login/dologin/", "POST", data, cookies.toString(), true,
                    "https://steamcommunity.com/login/home/?goto=", false);
            final String loginResponse2 = String.valueOf(res.getResponse());
            if (null != res.getCookies()) {
                cookies.append(res.getCookies());
            }
            DoLoginResultBean doLoginResult2 = JSONObject.parseObject(loginResponse2, DoLoginResultBean.class);
            if (!doLoginResult2.isLogin_complete()) {
                log.error("登录失败:" + loginResponse2);
                return new StringBuilder();
            }
            res = Http.request("https://steamcommunity.com/market/eligibilitycheck/?goto=%2Fid%2Fcsgo%2Ftradeoffers%2F",
                    "GET", null, cookies.toString(), true, "http://steamcommunity.com/id/csgo/tradeoffers/sent/", true);
            if (null != res.getCookies()) {
                cookies.append(res.getCookies());
            }
            steamUserDate.setCookies(cookies);
            log.info(steamUserDate.getAccount_name() + " 登录成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("登录失败:" + e.getMessage());
            return new StringBuilder();
        }
        return cookies;
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
        access_token = steamUserDate.getSession().getSteamID() + "%7C%7C" + access_token;
        return access_token;
    }

    public String getCookies(SteamUserDate steamUserDate) {
        HashMap<Object, Object> map = new HashMap() {{
            put("steamLoginSecure", get_access_token(steamUserDate));
            put("sessionid", steamUserDate.getSession().getSessionID());
            put("Steam_Language", "english");
            put("timezoneOffset", "28800,0");
            put("_ga", "GA1.2.234547838.1688523763");
            put("browserid", "'2685889387687629642'");
            put("strInventoryLastContext", "2504460_2");
        }};
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
