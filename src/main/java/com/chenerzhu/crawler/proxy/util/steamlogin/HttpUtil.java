package com.chenerzhu.crawler.proxy.util.steamlogin;

import org.apache.http.client.methods.HttpRequestBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by Mr.W on 2017/5/15.
 * about http connecting
 */
public class HttpUtil {

    /**
     * GET method
     */
    public static final String METHOD_GET = "GET";
    /**
     * POST method
     */
    public static final String METHOD_POST = "POST";
    private static final String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String contentType = "application/x-www-form-urlencoded; charset=UTF-8";
    private static final String acceptEncoding = "gzip, deflate, br";
    private static final String acceptLanguage = "q=0.8,en-US;q=0.5,en;q=0.3";
    private static final String cacheControl = "max-age=0";
    private static final String connection = "keep-alive";
    private static final String host = "steamcommunity.com";
    private static final String upgradeInsecureRequests = "1";
    private static final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/603.3.4 (KHTML, like Gecko) Version/10.1.2 Safari/603.3.4";

    /**
     * input流转string
     */
    public static String getContent(InputStream inputStream) {
        InputStreamReader inputStreamReader;
        inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String buff;
        StringBuilder content = new StringBuilder();
        try {
            while (null != (buff = bufferedReader.readLine())) {
                content.append(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return content.toString();
    }

    public static void addHeader(HttpRequestBase method, String cookies, String referer) {
        method.setHeader("Upgrade-Insecure-Requests", upgradeInsecureRequests);
        method.setHeader("Accept", accept);
        method.setHeader("Content-Type", contentType);
        method.setHeader("Accept-Encoding", acceptEncoding);
        method.setHeader("Accept-Language", acceptLanguage);
        method.setHeader("Cache-Control", cacheControl);
        method.setHeader("Connection", connection);
        method.setHeader("Origin", host);
        method.setHeader("User-Agent", userAgent);
        if (null != cookies && !cookies.trim().equals(""))
            method.setHeader("Cookie", cookies.substring(0, cookies.lastIndexOf(";")));
        if (null != referer) {
            method.setHeader("Referer", referer);
        }
    }
}
