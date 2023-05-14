package com.chenerzhu.crawler.proxy.pool.csgo.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.ProductList;
import com.chenerzhu.crawler.proxy.pool.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.pool.util.HttpsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import sun.net.www.protocol.https.Handler;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;

@Slf4j
@Component
public class HttpsSendUtil {

    public Object send(String ip, int port, String searchUrl, Map<String, String> headMap) {
        boolean available = false;
        HttpURLConnection connection = null;
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(searchUrl);
            Proxy proxy = null;
            if (StrUtil.isEmpty(ip)){
                String htmlDate = HttpClientUtils.sendGet(searchUrl,headMap);
                return htmlDate;
            }else {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            }
            connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setRequestProperty("accept", "");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
            for (Map.Entry<String, String> entry : headMap.entrySet()) {
                connection.setRequestProperty(entry.getKey(),entry.getValue());
            }
            connection.setConnectTimeout(2 * 1000);
            connection.setReadTimeout(3 * 1000);
            connection.setInstanceFollowRedirects(false);

//            // 设置私有代理-认证头部
//            if(StringUtils.isNotEmpty(PRIVATE_USERNAME) && StringUtils.isNotEmpty(PRIVATE_PASSWORD)){
//                final String userName = PRIVATE_USERNAME;
//                final String password = PRIVATE_PASSWORD;
//                String nameAndPass = userName +":"+ password;
//                String encoding =new String(Base64.encodeBase64(nameAndPass.getBytes()));
//                connection.setRequestProperty("Proxy-Authorization","Basic " + encoding);
//            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String s = null;

            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            if (connection.getResponseCode() == 200) {
                available = true;
            }
            ProductList productList = JSONObject.parseObject(sb.toString(), ProductList.class);
            log.info("validateHttp ==> ip:{} port:{} info:{}", ip, port, connection.getResponseMessage());
        } catch (Exception e) {
            e.printStackTrace();
            available = false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        if (!available){
            return "";
        }
        return sb.toString();
    }
}
