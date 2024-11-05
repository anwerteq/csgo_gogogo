package com.chenerzhu.crawler.proxy.util.steamlogin;


import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.applicationRunners.SteamApplicationRunner;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.protobufs.steammessages_auth.SteammessagesAuth;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import com.google.protobuf.InvalidProtocolBufferException;

import javax.crypto.Cipher;
import java.security.PublicKey;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.util.JsonFormat;
import feign.HeaderMap;

public class SteamLoginUtilTest {

    // steam登录标识
    public static ThreadLocal<Boolean> steamLoginUrlFlag = new ThreadLocal<>();

    // steam登录的的请求头数据
    public static ThreadLocal< Map<String, String>> steamLoginHeaderMapThreadLocal = new ThreadLocal<>();
    static {
        steamLoginHeaderMapThreadLocal.set(new HashMap<>());
    }

    public static void main1(String[] args) {
        String basestr = "CMTo8OCx27z1dhIQJyBMQnntWn2lGZ6YDe/YSB0AAKBAIgIIAyj5/56XlYCAiAEy2gNleUFpZEhsd0lqb2dJa3BYVkNJc0lDSmhiR2NpT2lBaVJXUkVVMEVpSUgwLmV5QWlhWE56SWpvZ0ltTTZPRFUyT0RreU9ESTJNemt3T1RFMk1EQXdOQ0lzSUNKemRXSWlPaUFpTnpZMU5qRXhPVGt6TlRFeE9EVTBNREVpTENBaVlYVmtJam9nV3lBaWQyVmhheUlnWFN3Z0ltVjRjQ0k2SURFM01qazNOak0yTmpBc0lDSnVZbVlpT2lBd0xDQWlhV0YwSWpvZ01UY3lPVGMyTWpjMk1Dd2dJbXAwYVNJNklDSXhNREpCWHpJMU5ERkRRVEJGWDBORVFUUTVJaXdnSW05aGRDSTZJREUzTWprM05qSTNOakFzSUNKeWRGOWxlSEFpT2lBd0xDQWlhWEJmYzNWaWFtVmpkQ0k2SUNJeU1Ua3VOemt1TVRBMkxqSXpNU0lzSUNKcGNGOWpiMjVtYVhKdFpYSWlPaUFpTWpFNUxqYzVMakV3Tmk0eU16RWlJSDAuZnBDNGowS1hmU3dhT1NjUmw0czZaUU1hRENmZi04UGlyWFpHbGhKcFBENWxFVTltSFdsYm4tOXhvSDUtSV8wYnpHN0Fkanh0MjhpcE82UG93VDIwQVFCAA==";
        byte[] decode = Base64.getDecoder().decode(basestr);
        try {
            SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response beginAuthSessionViaCredentialsResponse = SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response.parseFrom(decode);
            String jsonString = JsonFormat.printer().print(beginAuthSessionViaCredentialsResponse);
            System.out.println(beginAuthSessionViaCredentialsResponse.getAllowedConfirmations(0));
            System.out.println(jsonString);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        System.out.println("123123");
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        steamLoginUrlFlag.set(true);
        SteamApplicationRunner.steamUserDates.add(new SteamUserDate());
        step1();
        SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Response step2Value = step2();
        String encryptPasswordProtobuf = encryptPasswordProtobuf(step2Value.getPublickeyExp(), step2Value.getPublickeyMod(), "QingLiu98!");
        step3("mu64kkro", encryptPasswordProtobuf, Long.valueOf(step2Value.getTimestamp()));
        // CNnRl9QWT9leRnGksIuwzJBLzfzX9OFKGNMzl+sXOhWUvlFt0dN1xfwD5Hs0Isq/QOSCRIomOcCNKoodByZzwP1UzIOxmqtsA2fHv/2E0gzFLmy5cAbBKq4eQE5MgS2p03ALr5GIWv5tMrFSec+o2rr+aXn9yqYFzY8OfFdOK+/xgNM07Nwpx1rRViTCZVn1d3wtRMNqPwyCTDRLleM4EC9DHJyK3XaKGICa1aBXMvMLOGMJEHa8wwWCNoUaBB5s

    }

    public static void step1() throws UnsupportedEncodingException {
        String url = "https://steamcommunity.com";
        String response = HttpClientUtils.sendGet(url, new HashMap<>(), new HashMap<>());
    }


    /**
     * @return
     * @throws UnsupportedEncodingException
     */
    public static SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Response step2() throws UnsupportedEncodingException {
        String url = "https://api.steampowered.com/IAuthenticationService/GetPasswordRSAPublicKey/v1";
        SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Request accountProtobufs = SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Request.newBuilder().setAccountName("mu64kkro").build();
        steamLoginUrlFlag.set(true);
        // Base64 编码
        String base64EncodedMessage = Base64.getEncoder().encodeToString(accountProtobufs.toByteArray());
        // 序列化为字节数组
        Map<String, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("input_protobuf_encoded", base64EncodedMessage);
        Map<String, String> headerMap =  SteamLoginUtilTest.steamLoginHeaderMapThreadLocal.get();
        headerMap.put("Referer", "https://steamcommunity.com");
        headerMap.put("Cookie", CookiesConfig.steamCookies.get());
        String response = HttpClientUtils.sendGet(url, headerMap, objectObjectHashMap);
        byte[] decode = Base64.getDecoder().decode(response);
        SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Response rsaPublicKeyResponse = null;
        try {
            rsaPublicKeyResponse = SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Response.parseFrom(decode);
            String jsonString = JsonFormat.printer().print(rsaPublicKeyResponse);
            System.out.println(rsaPublicKeyResponse);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        return rsaPublicKeyResponse;
    }

    public static void step3(String account_name, String encrypted_password, Long rsa_timestamp) {

        SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Request credentialsRequest = SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Request
                .newBuilder()
                .setAccountName(account_name)
                .setEncryptedPassword(encrypted_password)
                .setEncryptionTimestamp(rsa_timestamp)
                .setRememberLogin(true)
                .setPlatformType(SteammessagesAuth.EAuthTokenPlatformType.k_EAuthTokenPlatformType_MobileApp)
                .setWebsiteId("'Community'")
                .setPersistence(SteammessagesAuth.ESessionPersistence.k_ESessionPersistence_Persistent)
                .setDeviceFriendlyName("Mozilla/5.0 (X11; Linux x86_64; rv:1.9.5.20) Gecko/2812-12-10 04:56:28 Firefox/3.8")
                .build();
        String url = "https://api.steampowered.com/IAuthenticationService/BeginAuthSessionViaCredentials/v1";
        // Base64 编码
        String base64EncodedMessage = Base64.getEncoder().encodeToString(credentialsRequest.toByteArray());
        // 序列化为字节数组
        Map<String, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("input_protobuf_encoded", base64EncodedMessage);
        Map<String, String> headerMap = SteamLoginUtilTest.steamLoginHeaderMapThreadLocal.get();
        headerMap.put("Origin", "https://steamcommunity.com");
        headerMap.put("Referer", "https://steamcommunity.com");
        headerMap.put("Cookie", CookiesConfig.steamCookies.get());
        headerMap.put("Content-Type", "application/x-www-form-urlencoded"); // 设置请求头为 JSON
        headerMap.put("Accept-Encoding", "gzip, deflate");
        headerMap.put("User-Agent", "python-requests/2.32.3");

        String response = HttpClientUtils.sendPostForm(url,"", headerMap,objectObjectHashMap);
// {'User-Agent': 'python-requests/2.32.3', 'Accept-Encoding': 'gzip, deflate', 'Accept': '*/*', 'Connection': 'keep-alive', 'Referer': 'https://steamcommunity.com/', 'Origin': 'https://steamcommunity.com', 'Content-Length': '645', 'Content-Type': 'application/x-www-form-urlencoded'}
        byte[] decode = Base64.getDecoder().decode(response);
        try {
            SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response beginAuthSessionViaCredentialsResponse = SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response.parseFrom(decode);
            System.out.println("12312");

        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        System.out.println("12312");
    }

    /**
     * steam登录需对密码加密
     *
     * @param publickeyExp
     * @param publickeyMod
     * @return
     * @throws Exception
     */
    public static String encryptPasswordProtobuf(String publickeyExp, String publickeyMod, String password) {
        // 将十六进制字符串转换为 BigInteger
        BigInteger publicKeyExp = new BigInteger(publickeyExp, 16);
        BigInteger publicKeyMod = new BigInteger(publickeyMod, 16);

        try {
            // 创建 RSA 公钥
            RSAPublicKeySpec spec = new RSAPublicKeySpec(publicKeyMod, publicKeyExp);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(spec);

            // 加密密码
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedPasswordBytes = cipher.doFinal(password.getBytes("ASCII"));
            // Base64 编码
            return Base64.getEncoder().encodeToString(encryptedPasswordBytes);
        } catch (Exception e) {
            throw new RuntimeException("encryptPasswordProtobuf error", e);
        }
    }

}
