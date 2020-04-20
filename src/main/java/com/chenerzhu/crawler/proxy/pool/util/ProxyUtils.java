package com.chenerzhu.crawler.proxy.pool.util;


import com.chenerzhu.crawler.proxy.pool.entity.ProxyConfig;
import com.chenerzhu.crawler.proxy.pool.service.IProxyConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.net.www.protocol.https.Handler;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenerzhu
 * @create 2018-09-05 21:14
 **/
@Slf4j
@Component
public final class ProxyUtils {

    /** 验证代理IP网址 只能是 http网址 不要使用 http转https会有302错误 */
    private static String VALIDATE_URL = "http://www.baidu.com";

    /** 私有接口-认证用户名 */
    private static String PRIVATE_USERNAME = "";


    /** 私有接口-认证用户密码 */
    private static String PRIVATE_PASSWORD = "";


    @Autowired
    private IProxyConfigService proxyConfigService;

    /**
     * 初始化
     */
    @PostConstruct
    public void init(){
        ProxyConfig config = proxyConfigService.getConfig();
        if(null != config){
            VALIDATE_URL = config.getValidateUrl();
        }

    }


    public static boolean validateIp(String ip, int port, ProxyType proxyType) {
        boolean available = false;
        if (proxyType.getType().equalsIgnoreCase("http")) {
            available = validateHttp(ip, port);
        } else if (proxyType.getType().equalsIgnoreCase("https")) {
            available = validateHttps(ip, port);
        }
        return available;
    }

    public static boolean validateHttp(String ip, int port) {
        boolean available = false;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(VALIDATE_URL);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setRequestProperty("accept", "");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
            connection.setConnectTimeout(2 * 1000);
            connection.setReadTimeout(3 * 1000);
            connection.setInstanceFollowRedirects(false);

            // 设置私有代理-认证头部
            if(StringUtils.isNotEmpty(PRIVATE_USERNAME) && StringUtils.isNotEmpty(PRIVATE_PASSWORD)){
                final String userName = PRIVATE_USERNAME;
                final String password = PRIVATE_PASSWORD;
                String nameAndPass = userName +":"+ password;
                String encoding =new String(Base64.encodeBase64(nameAndPass.getBytes()));
                connection.setRequestProperty("Proxy-Authorization","Basic " + encoding);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String s = null;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            if (connection.getResponseCode() == 200) {
                available = true;
            }
            log.info("validateHttp ==> ip:{} port:{} info:{}", ip, port, connection.getResponseMessage());
        } catch (Exception e) {
            //e.printStackTrace();
            available = false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return available;
    }

    public static boolean validateHttps(String ip, int port) {
        boolean available = false;
        HttpsURLConnection httpsURLConnection = null;
        try {
            URL url = new URL(null, VALIDATE_URL, new Handler());
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            httpsURLConnection = (HttpsURLConnection) url.openConnection(proxy);
            httpsURLConnection.setSSLSocketFactory(HttpsUtils.getSslSocketFactory());
            httpsURLConnection.setHostnameVerifier(HttpsUtils.getTrustAnyHostnameVerifier());
            httpsURLConnection.setRequestProperty("accept", "");
            httpsURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpsURLConnection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
            httpsURLConnection.setConnectTimeout(2 * 1000);
            httpsURLConnection.setReadTimeout(3 * 1000);
            httpsURLConnection.setInstanceFollowRedirects(false);

            // 设置私有代理-认证头部
            if(StringUtils.isNotEmpty(PRIVATE_USERNAME) && StringUtils.isNotEmpty(PRIVATE_PASSWORD)){
                final String userName = PRIVATE_USERNAME;
                final String password = PRIVATE_PASSWORD;
                String nameAndPass = userName +":"+ password;
                String encoding =new String(Base64.encodeBase64(nameAndPass.getBytes()));
                httpsURLConnection.setRequestProperty("Proxy-Authorization","Basic " + encoding);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            String s = null;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            if (httpsURLConnection.getResponseCode() == 200) {
                available = true;
            }
            log.info("validateHttps ==> ip:{} port:{} info:{}", ip, port, httpsURLConnection.getResponseMessage());
        } catch (Exception e) {
            //e.printStackTrace();
            available = false;
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        }
        return available;
    }

    public static void main(String[] args) {
        AtomicInteger counter=new AtomicInteger(0);
        CountDownLatch latch=new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String ip = "115.216.43.251";
                    int port = 15238;
                    boolean availableHttp = ProxyUtils.validateHttp(ip, port);
                    boolean availableHttps = ProxyUtils.validateHttps(ip, port);
                    if(availableHttp||availableHttps){
                        counter.incrementAndGet();
                    }
                    latch.countDown();
                    System.out.println("http:" + availableHttp + " https:" + availableHttps);
                }
            });
            thread.start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("========"+counter.get()/100.0);
    }

    public enum ProxyType {
        HTTP("HTTP"),
        HTTPS("HTTPS"),
        SOCKS("SOCKS");
        private String type;

        ProxyType(String proxyType) {
            this.type = proxyType;
        }

        public String getType() {
            return type;
        }
    }

}