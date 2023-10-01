package com.chenerzhu.crawler.proxy.util.steamlogin;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class SteamLoginUtil {
    public static void main(String[] args) {
        String folderPath = "D:\\csgo文件（不能删除）\\SDA-1.0.13\\maFiles";

        readFilesInFolder(folderPath);
    }

    public static void readFilesInFolder(String folderPath) {

        List<SteamUserDate> arrayList = new ArrayList();
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().contains(".maFile") && file.getName().contains("-")) {
                        arrayList.add(readJsonFromFile(file));
                    } else if (file.isDirectory()) {
                        readFilesInFolder(file.getAbsolutePath());
                    }
                }
            }
        } else {
            System.out.println("Invalid folder path");
        }
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
            String password = file.getName().split("-")[1].split("\\.")[0];
            steamUserDate.setUserPsw(password);
            login(steamUserDate);
            // 在这里对 jsonObject 进行你需要的处理
            System.out.println(JSONObject.parseObject(jsonString));
            return steamUserDate;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean login(SteamUserDate steamUserDate) {
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
                log.error(rsaResponse1);
                throw new LoginException("get rsa_key1 error");
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
            res = Http.request("https://steamcommunity.com/login/dologin/", "POST", data, cookies.toString(), true,
                    "https://steamcommunity.com/login/home/?goto=", false);
            if (429 == res.getCode()) {
                log.info("因访问steam太频繁，[" + steamUserDate.getAccount_name() + "]尝试重新登录");
                SleepUtil.sleep(3000);
            }
            final String loginResponse1 = String.valueOf(res.getResponse());

            DoLoginResultBean doLoginResult1 = JSONObject.parseObject(loginResponse1, DoLoginResultBean.class);

            if (!doLoginResult1.isRequires_twofactor()) {

                throw new LoginException(doLoginResult1.getMessage());
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
                return false;
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
            return false;
        }
        return true;

    }
}
