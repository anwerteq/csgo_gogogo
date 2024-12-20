package com.chenerzhu.crawler.proxy.util;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.applicationRunners.SteamApplicationRunner;
import com.chenerzhu.crawler.proxy.common.HttpMethod;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.steamlogin.SteamUserDate;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @author chenerzhu
 * @create 2018-08-11 11:25
 **/
@Slf4j
@Component
@Order(1)
public class HttpClientUtils implements ApplicationRunner {

    @Value("${proxyIp}")
    private String proxyIp;
    public static String staticProxyIp = "127.0.0.1:7890";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static RequestConfig reqConf = null;
    private static StandardHttpRequestRetryHandler standardHandler = null;

    public static String send(String url, String content, Map<String, String> headerMap, Map<String, String> formParamMap, String contentCharset, String resultCharset, HttpMethod method) {
        if (StringUtils.isEmpty(contentCharset)) {
            contentCharset = DEFAULT_CHARSET;
        }
        CloseableHttpClient httpClient = null;
        HttpResponse httpResponse = null;
        try {
            // 创建 CookieStore
            CookieStore cookieStore = new BasicCookieStore();
            if (StrUtil.isNotEmpty(staticProxyIp)) {
                //设置代理IP、端口
                HttpHost proxy = new HttpHost(staticProxyIp.split(":")[0], Integer.parseInt(staticProxyIp.split(":")[1]));
                DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
//            httpClient = HttpClients.custom().build();
                httpClient = HttpClients.custom().setRoutePlanner(routePlanner).setSSLSocketFactory(getSSL()).setDefaultCookieStore(cookieStore).build();
            } else {
                httpClient = HttpClients.custom().setSSLSocketFactory(getSSL()).setDefaultCookieStore(cookieStore).build();
            }


            if (url.toLowerCase().startsWith("https")) {
//                initSSL(httpClient, getPort(url));
            }
            switch (method) {
                case GET:
                    if (formParamMap != null && !formParamMap.isEmpty() && !url.contains("?")) {
                        if (!url.endsWith("?")) {
                            url = url + "?";
                        }
                        for (Map.Entry<String, String> entry : formParamMap.entrySet()) {
                            url = url + entry.getKey() + "=" + java.net.URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
                        }
                    }
                    HttpGet httpGet = new HttpGet(url);
                    httpGet.setConfig(reqConf);
                    addHeader(httpGet, headerMap);
                    httpResponse = httpClient.execute(httpGet);
                    break;
                case POST:
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setConfig(reqConf);
                    addHeader(httpPost, headerMap);
                    if (formParamMap == null || formParamMap.isEmpty()) {
                        httpPost.setEntity(new StringEntity(content, contentCharset));
                    } else {
                        List<NameValuePair> ls = new ArrayList<NameValuePair>();
                        for (Map.Entry<String, String> param : formParamMap.entrySet()) {
                            ls.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                        }
                        httpPost.setEntity(new UrlEncodedFormEntity(ls, "UTF-8"));
                    }
                    httpResponse = httpClient.execute(httpPost);
                    break;
                case DELETE:
                    HttpDelete httpDelete = new HttpDelete(url);
                    httpDelete.setConfig(reqConf);
                    addHeader(httpDelete, headerMap);
                    httpResponse = httpClient.execute(httpDelete);
                    break;
                case PUT:
                    HttpPut httpPut = new HttpPut(url);
                    httpPut.setConfig(reqConf);
                    addHeader(httpPut, headerMap);
                    httpPut.setEntity(new StringEntity(content, contentCharset));
                    httpResponse = httpClient.execute(httpPut);
                    break;
                case PATCH:
                    HttpPatch httpPatch = new HttpPatch(url);
                    httpPatch.setConfig(reqConf);
                    addHeader(httpPatch, headerMap);
                    httpPatch.setEntity(new StringEntity(content, contentCharset));
                    httpResponse = httpClient.execute(httpPatch);
                    break;
            }
            int statusCode = httpResponse.getStatusLine().getStatusCode();
//            log.info("request url：" + url + "; response status：" + httpResponse.getStatusLine());
            if (statusCode == 200) {
                BufferedHttpEntity entity = new BufferedHttpEntity(httpResponse.getEntity());
                //获取响应内容
                if (StringUtils.isEmpty(resultCharset)) {
                    resultCharset = DEFAULT_CHARSET;
                }
                SteamUserDate steamUserDate = SteamTheadeUtil.steamUserDateTL.get();
                if ( steamUserDate != null){
                    Map<String, String> newHeader = new HashMap<>();
                    for (Cookie cookie : cookieStore.getCookies()) {
                        newHeader.put(cookie.getName(), cookie.getValue());
                    }
                    steamUserDate.refreshCookies(newHeader);
                }

                return EntityUtils.toString(entity, resultCharset);
            }
            if (statusCode == 429) {
                log.info("代理ip访问steam频繁，可以通过切换clash节点，避免访问频繁");
                log.info("因访问steam频繁,进行睡眠60s，此提示过多,就一定要切换clash的节点");
                SleepUtil.sleep(60 * 1000);
                return null;
            }
            if (statusCode != 200) {
                BufferedHttpEntity entity = new BufferedHttpEntity(httpResponse.getEntity());
                //获取响应内容
                if (StringUtils.isEmpty(resultCharset)) {
                    resultCharset = DEFAULT_CHARSET;
                }
                log.info("访问发生异常,异常码是:{},返回信息是:{}", statusCode, EntityUtils.toString(entity, resultCharset));
                return null;
            }
        } catch (ClientProtocolException e) {
            log.error("Protocol error", e);
        } catch (IOException e) {
            log.error("Network error", e);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static {
        reqConf = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .setRedirectsEnabled(true)
                .setMaxRedirects(2)
                .build();
        standardHandler = new StandardHttpRequestRetryHandler(3, true);
    }

    public static void requestConfig() {
        reqConf = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(2000)
                .setRedirectsEnabled(false)
                .setMaxRedirects(0)
                .build();
        standardHandler = new StandardHttpRequestRetryHandler(3, true);
    }

    public static SSLConnectionSocketFactory getSSL() {
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        X509TrustManager tm = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0,
                                           String arg1) throws CertificateException {
            }
        };
        try {
            ctx.init(null, new TrustManager[]{tm}, null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
        return ssf;
    }

    /**
     * 添加请求头
     *
     * @param httpRequest
     * @param headerMap
     * @return
     */
    private static HttpRequestBase addHeader(HttpRequestBase httpRequest, Map<String, String> headerMap) {
        if (headerMap != null && !headerMap.isEmpty()) {
            Set<String> keys = headerMap.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                httpRequest.addHeader(key, headerMap.get(key));
            }
        }
        return httpRequest;
    }

    /**
     * @param url 路径
     * @return int
     * @author
     * @date
     */
    private static int getPort(String url) {
        int startIndex = url.indexOf("://") + "://".length();
        String host = url.substring(startIndex);
        if (host.indexOf("/") != -1) {
            host = host.substring(0, host.indexOf("/"));
        }
        int port = 443;
        if (host.contains(":")) {
            int i = host.indexOf(":");
            port = new Integer(host.substring(i + 1));
        }
        return port;
    }

    /**
     * 初始化HTTPS请求服务
     *
     * @param httpClient HTTP客户端
     * @param port       端口
     */
    private static void initSSL(CloseableHttpClient httpClient, int port) {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            final X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            // 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            ConnectionSocketFactory ssf = new SSLConnectionSocketFactory(sslContext);
            Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create().register("https", ssf).build();
            BasicHttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(r);
            HttpClients.custom().setConnectionManager(ccm).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String sendGet(final String url, Map<String, String> headerMap, Map<String, String> paramMap) {
        return send(url, "", headerMap, paramMap, DEFAULT_CHARSET, DEFAULT_CHARSET, HttpMethod.GET);
    }

    public static String sendGet(final String url, Map<String, String> headerMap) {
        return sendGet(url, headerMap, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    public static String sendGet(final String url, Map<String, String> headerMap, String contentCharset, String resultCharset) {
        return send(url, "", headerMap, null, contentCharset, resultCharset, HttpMethod.GET);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始测试代理是否可以访问steam");
        if (StrUtil.isEmpty(proxyIp)) {
            staticProxyIp = "";
            log.info("代理ip:host代理");
        } else {
            staticProxyIp = proxyIp;
            log.info("代理ip为：" + proxyIp);
        }

        String url = "https://steamcommunity.com/market/priceoverview/?country=US&currency=1&appid=730&market_hash_name=" + URLEncoder.encode("Sticker | Mahjong Zhong", "UTF-8");
        String reponse = sendGet(url, new HashMap<>());

        log.info("代理IP可以成功访问steam,测试接口返回的数据为：" + reponse);
    }

    public static String sendPost(final String url, String content, Map<String, String> headerMap) {
        return sendPost(url, content, headerMap, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    public static String sendPostForm(final String url, String content, Map<String, String> headerMap, Map<String, String> formParamMap) {
        return send(url, content, headerMap, formParamMap, DEFAULT_CHARSET, DEFAULT_CHARSET, HttpMethod.POST);
    }

    public static String sendPost(final String url, String content, Map<String, String> headerMap, String contentCharset, String resultCharset) {
        return send(url, content, headerMap, null, contentCharset, resultCharset, HttpMethod.POST);
    }

    public static String sendPostForm(final String url, String content, Map<String, String> headerMap, Map<String, String> formParamMap, String contentCharset, String resultCharset) {
        return send(url, content, headerMap, formParamMap, contentCharset, resultCharset, HttpMethod.POST);
    }

    public static String sendDelete(final String url, String content, Map<String, String> headerMap) {
        return sendDelete(url, content, headerMap, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    public static String sendDelete(final String url, String content, Map<String, String> headerMap, String contentCharset, String resultCharset) {
        return send(url, content, headerMap, null, contentCharset, resultCharset, HttpMethod.DELETE);
    }

    public static String sendPut(final String url, String content, Map<String, String> headerMap) {
        return sendPut(url, content, headerMap, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    public static String sendPut(final String url, String content, Map<String, String> headerMap, String contentCharset, String resultCharset) {
        return send(url, content, headerMap, null, contentCharset, resultCharset, HttpMethod.PUT);
    }

    public static String sendPatch(final String url, String content, Map<String, String> headerMap) {
        return sendPatch(url, content, headerMap, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    public static String sendPatch(final String url, String content, Map<String, String> headerMap, String contentCharset, String resultCharset) {
        return send(url, content, headerMap, null, contentCharset, resultCharset, HttpMethod.PATCH);
    }

    public static void main(String[] args) {
        String result = HttpClientUtils.sendGet("https://www.baidu.com", null);
        System.out.println(result);
    }


}
