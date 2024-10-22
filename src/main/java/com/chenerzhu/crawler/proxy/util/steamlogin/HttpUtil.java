package com.chenerzhu.crawler.proxy.util.steamlogin;

import com.chenerzhu.crawler.proxy.protobufs.CAuthenticationGetPasswordRSAPublicKeyResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
    private static final String host = "https://steamcommunity.com";
    private static final String upgradeInsecureRequests = "1";
    private static final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/603.3.4 (KHTML, like Gecko) Version/10.1.2 Safari/603.3.4";

    /**
     * input流转string
     */
    public static String getContent(InputStream inputStream) {
        try {
            byte[] bytes = readInputStream(inputStream);
            //steam登录
            if (SteamLoginUtilTest.steamLoginUrlFlag.get()){
                String base64Encode = Base64.getEncoder().encodeToString(bytes);
                return base64Encode;
            }
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024]; // 创建一个缓冲区

        int bytesRead;
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead); // 将读取的数据写入缓冲区
        }
        buffer.flush(); // 确保所有数据都被写入
        return buffer.toByteArray(); // 转换为字节数组
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
