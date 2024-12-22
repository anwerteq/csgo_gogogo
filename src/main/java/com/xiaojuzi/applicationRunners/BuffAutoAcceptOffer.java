//package com.chenerzhu.crawler.proxy.applicationRunners;
//
//import cn.hutool.http.HttpUtil;
//import in.dragonbra.javasteam.steam.steamclient.SteamClient;
//import lombok.extern.slf4j.Slf4j;
//
//import java.net.http.HttpResponse;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.*;
//import java.time.*;
//import java.io.*;
//import java.net.*;
//
//@Slf4j
//public class BuffAutoAcceptOffer {
//
//    private static final Map<String, String> BUFF_HEADERS = new HashMap<>() {{
//        put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 Edg/105.0.1343.27");
//    }};
//
//    private SteamClient steamClient;
//    private boolean developmentMode;
//    private Map<String, Float> lowestOnSalePriceCache = new HashMap<>();
//    private Map<String, Object> orderInfo = new HashMap<>();
//
//
//
//    public boolean init() {
//        String session = BuffHelper.getValidSessionForBuff(steamClient, logger);
//        return session.isEmpty();
//    }
//
//    public void requireBuyerSendOffer() {
//        String url = "https://buff.163.com/account/api/prefer/force_buyer_send_offer";
//        Map<String, String> data = Map.of("force_buyer_send_offer", "true");
//
//        try {
//            HttpResponse<String> response = HttpUtil.get("https://buff.163.com/api/market/steam_trade", BUFF_HEADERS);
//            String csrfToken = HttpUtil.extractCookie(response, "csrf_token");
//
//            Map<String, String> headers = new HashMap<>(BUFF_HEADERS);
//            headers.put("X-CSRFToken", csrfToken);
//            headers.put("Origin", "https://buff.163.com");
//            headers.put("Referer", "https://buff.163.com/user-center/profile");
//
//            HttpResponse<String> postResponse = HttpUtil.post(url, headers, data);
//            if (postResponse.statusCode() == 200 && "OK".equals(JsonParser.parse(postResponse.body()).get("code"))) {
//                logger.info("已开启买家发起交易报价功能");
//            } else {
//                logger.error("开启买家发起交易报价功能失败");
//            }
//        } catch (Exception e) {
//            logger.error("开启买家发起交易报价功能失败", e);
//        }
//    }
//
//    public void exec() {
//        logger.info("BUFF自动接受报价插件已启动.请稍候...");
//
//        try {
//            String cookies = Files.readString(Path.of("BUFF_COOKIES_FILE_PATH"));
//            BUFF_HEADERS.put("Cookie", cookies.split(";")[0]);
//            logger.info("已检测到cookies, 尝试登录");
//
//            String userName = checkBuffAccountState(developmentMode);
//            if (userName.isEmpty()) {
//                logger.error("由于登录失败,插件自动退出");
//                System.exit(1);
//                return;
//            }
//
//            if (!steamClient.getSteam64IdFromCookies().equals(getBuffBindSteamId())) {
//                logger.error("当前登录账号与BUFF绑定的Steam账号不一致!");
//                System.exit(1);
//                return;
//            }
//
//            logger.info("已经登录至BUFF 用户名: " + userName);
//            requireBuyerSendOffer();
//
//            while (true) {
//                synchronized (steamClientMutex) {
//                    if (!steamClient.isSessionAlive()) {
//                        logger.info("Steam会话已过期, 正在重新登录...");
//                        steamClient.clearCookies();
//                        steamClient.login();
//                        logger.info("Steam会话已更新");
//                    }
//                }
//
//                logger.info("正在进行BUFF待发货/待收货饰品检查...");
//                userName = checkBuffAccountState();
//                if (userName.isEmpty()) {
//                    logger.error("BUFF账户登录状态失效, 无法自动重新登录!");
//                    return;
//                }
//
//                // Additional logic for checking and processing orders goes here
//
//                Thread.sleep(config.getInt("buff_auto_accept_offer.interval") * 1000);
//            }
//        } catch (Exception e) {
//            logger.error("执行时出现错误", e);
//        }
//    }
//
//    private String checkBuffAccountState(boolean dev) {
//        // Simulate account state check logic here
//        return "";
//    }
//
//    private String getBuffBindSteamId() {
//        // Simulate Buff Steam ID retrieval
//        return "";
//    }
//
//    public static void main(String[] args) {
//        // Setup logger, config, and other dependencies here
//        Logger logger = new Logger();
//        SteamClient steamClient = new SteamClient();
//        Object mutex = new Object();
//        Config config = new Config();
//
//        BuffAutoAcceptOffer buffAutoAcceptOffer = new BuffAutoAcceptOffer(logger, steamClient, mutex, config);
//        if (!buffAutoAcceptOffer.init()) {
//            buffAutoAcceptOffer.exec();
//        }
//    }
//}
