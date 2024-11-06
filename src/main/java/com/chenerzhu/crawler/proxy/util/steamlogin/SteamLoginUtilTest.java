package com.chenerzhu.crawler.proxy.util.steamlogin;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.applicationRunners.SteamApplicationRunner;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.protobufs.steammessages_auth.SteammessagesAuth;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import com.google.protobuf.InvalidProtocolBufferException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
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

import static com.chenerzhu.crawler.proxy.protobufs.steammessages_auth.SteammessagesAuth.EAuthSessionGuardType.k_EAuthSessionGuardType_DeviceConfirmation;
import static com.sun.org.apache.xalan.internal.xsltc.compiler.Constants.CHARACTERS;


public class SteamLoginUtilTest {

    // steam登录标识
    public static ThreadLocal<Boolean> steamLoginUrlFlag = new ThreadLocal<>();

    // steam登录的的请求头数据
    public static ThreadLocal<Map<String, String>> steamLoginHeaderMapThreadLocal = new ThreadLocal<>();

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
        SteamUserDate steamUserDate = new SteamUserDate();
        steamUserDate.setAccount_name("mu64kkro");
        steamUserDate.setUserPsw("QingLiu98!");
        steamUserDate.setShared_secret("vbAREhPkibtwemEklyePZH2b73c=");
        steamUserDate.getSession().setSteamID("76561199351185401");
        SteamApplicationRunner.steamUserDateTL.set(steamUserDate);
        step1();
        SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Response step2Value = step2();
        String encryptPasswordProtobuf = encryptPasswordProtobuf(step2Value.getPublickeyExp(), step2Value.getPublickeyMod(), steamUserDate.getUserPsw());
        SleepUtil.sleep(4000);
        SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response authSessionViaCredentialsResponse =
                step3(steamUserDate.getAccount_name(), encryptPasswordProtobuf, Long.valueOf(step2Value.getTimestamp()));
        SteammessagesAuth.CAuthentication_AllowedConfirmation allowedConfirmations = authSessionViaCredentialsResponse.getAllowedConfirmations(0);
        if (allowedConfirmations != null) {
            //移动设备
            SteammessagesAuth.EAuthSessionGuardType confirmationType = allowedConfirmations.getConfirmationType();
            if (confirmationType.getNumber() == SteammessagesAuth.EAuthSessionGuardType.k_EAuthSessionGuardType_DeviceCode.getNumber()) {
                if (StrUtil.isEmpty(steamUserDate.getShared_secret())) {
                    //获取shared_secret值
                } else {
                    String one_time_code = generateOneTimeCode(steamUserDate.getShared_secret(), null);
                    steamUserDate.setOneTimeCode(one_time_code);
                    step4(authSessionViaCredentialsResponse);
                    step5(authSessionViaCredentialsResponse);
                    System.out.println("123123");
                }
            }
        }

    }

    /**
     * 生成认证码
     *
     * @param sharedSecret
     * @param timestamp
     * @return
     */
    public static String generateOneTimeCode(String sharedSecret, Long timestamp) {
        if (timestamp == null) {
            timestamp = System.currentTimeMillis() / 1000;
            timestamp += tryToGetTimeDeltaFromSteam(); // Implement this method as needed
        }
        // Pack the timestamp as Big Endian uint64
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(0, timestamp / 30);
        byte[] timeBuffer = buffer.array();

        // Decode the shared secret
        byte[] decodedKey = Base64.getDecoder().decode(sharedSecret);
        // Create HMAC using SHA-1
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "HmacSHA1");
            mac.init(keySpec);
            byte[] timeHmac = mac.doFinal(timeBuffer);
            // Calculate the offset
            int begin = timeHmac[19] & 0x0F;
            int fullCode = ((timeHmac[begin] & 0xFF) << 24) |
                    ((timeHmac[begin + 1] & 0xFF) << 16) |
                    ((timeHmac[begin + 2] & 0xFF) << 8) |
                    (timeHmac[begin + 3] & 0xFF);
            fullCode &= 0x7FFFFFFF; // 31 bits
            // Generate the code
            StringBuilder code = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                fullCode = fullCode / CHARACTERS.length();
                int index = fullCode % CHARACTERS.length();
                code.append(CHARACTERS.charAt(index));
            }
            return code.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static long tryToGetTimeDeltaFromSteam() {
        long timeDelta = 0;
        if (timeDelta == Long.MAX_VALUE) {
            for (int i = 0; i < 3; i++) {
                long serverTime = getSteamServerTime();
                if (serverTime != -1) {
                    timeDelta = serverTime - System.currentTimeMillis() / 1000; // Convert to seconds
                    return (int) timeDelta;
                }
            }
            timeDelta = 0;
        }
        return (int) timeDelta;
    }

    /**
     * 获取服务器时间
     *
     * @return
     */
    public static long getSteamServerTime() {
        String url = "https://api.steampowered.com/ITwoFactorService/QueryTime/v1/";
        Map<String, String> headerMap = SteamLoginUtilTest.steamLoginHeaderMapThreadLocal.get();
        headerMap.put("Referer", "https://steamcommunity.com");
        headerMap.put("Cookie", CookiesConfig.steamCookies.get());
        String response = HttpClientUtils.sendPost(url, "", headerMap);
        return 1;
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
        // Base64 编码
        String base64EncodedMessage = Base64.getEncoder().encodeToString(accountProtobufs.toByteArray());
        // 序列化为字节数组
        Map<String, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("input_protobuf_encoded", base64EncodedMessage);
        Map<String, String> headerMap = SteamLoginUtilTest.steamLoginHeaderMapThreadLocal.get();
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

    public static SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response step3(String account_name, String encrypted_password, Long rsa_timestamp) {

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

        String response = HttpClientUtils.sendPostForm(url, "", headerMap, objectObjectHashMap);
// {'User-Agent': 'python-requests/2.32.3', 'Accept-Encoding': 'gzip, deflate', 'Accept': '*/*', 'Connection': 'keep-alive', 'Referer': 'https://steamcommunity.com/', 'Origin': 'https://steamcommunity.com', 'Content-Length': '645', 'Content-Type': 'application/x-www-form-urlencoded'}
        byte[] decode = Base64.getDecoder().decode(response);
        try {
            SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response beginAuthSessionViaCredentialsResponse = SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response.parseFrom(decode);
            System.out.println("12312");
            return beginAuthSessionViaCredentialsResponse;

        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
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

    public static  void step4(SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response authSessionViaCredentialsResponse){
        SteamUserDate steamUserDate = SteamApplicationRunner.steamUserDateTL.get();
        Map<String, String> headerMap = SteamLoginUtilTest.steamLoginHeaderMapThreadLocal.get();
        headerMap.put("Cookie", CookiesConfig.steamCookies.get());
        headerMap.put("Content-Type", "application/x-www-form-urlencoded"); // 设置请求头为 JSON
        headerMap.put("Origin", "https://steamcommunity.com");
        headerMap.put("Referer", "https://steamcommunity.com");
        headerMap.remove("Content-Length");
        long clineId = authSessionViaCredentialsResponse.getClientId();
        SteammessagesAuth.CAuthentication_UpdateAuthSessionWithSteamGuardCode_Request build = SteammessagesAuth.CAuthentication_UpdateAuthSessionWithSteamGuardCode_Request.newBuilder()
                .setClientId(clineId)
                .setCode(steamUserDate.getOneTimeCode())
                .setSteamid(Long.valueOf(steamUserDate.getSession().getSteamID()))
                .setCodeTypeValue(k_EAuthSessionGuardType_DeviceConfirmation.getNumber()).build();
        String base64EncodedMessage = Base64.getEncoder().encodeToString(build.toByteArray());
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("input_protobuf_encoded",base64EncodedMessage);
        String url = "https://api.steampowered.com/IAuthenticationService/UpdateAuthSessionWithSteamGuardCode/v1";
        String response = HttpClientUtils.sendPostForm(url, "",headerMap,paraMap);
        if (!"".equals(response)){
            throw  new RuntimeException("执行报错");
        }
        System.out.println("!@#");
    }

    public static void step5(SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response authSessionViaCredentialsResponse){
        SteamUserDate steamUserDate = SteamApplicationRunner.steamUserDateTL.get();
        Map<String, String> headerMap = SteamLoginUtilTest.steamLoginHeaderMapThreadLocal.get();
        headerMap.put("Cookie", CookiesConfig.steamCookies.get());
        headerMap.put("Content-Type", "application/x-www-form-urlencoded"); // 设置请求头为 JSON
        headerMap.put("Origin", "https://steamcommunity.com");
        headerMap.put("Referer", "https://steamcommunity.com");
        headerMap.remove("Content-Length");
        SteammessagesAuth.CAuthentication_PollAuthSessionStatus_Request build = SteammessagesAuth.CAuthentication_PollAuthSessionStatus_Request.newBuilder()
                .setClientId(authSessionViaCredentialsResponse.getClientId())
                .setRequestId(authSessionViaCredentialsResponse.getRequestId()).build();
        String base64EncodedMessage = Base64.getEncoder().encodeToString(build.toByteArray());
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("input_protobuf_encoded",base64EncodedMessage);
        String url = "https://api.steampowered.com/IAuthenticationService/PollAuthSessionStatus/v1";
        String response = HttpClientUtils.sendPostForm(url, "",headerMap,paraMap);
        byte[] decode = Base64.getDecoder().decode(response);
        SteammessagesAuth.CAuthentication_PollAuthSessionStatus_Response pollAuthSessionStatusResponse = null;
        try {
            pollAuthSessionStatusResponse = SteammessagesAuth.CAuthentication_PollAuthSessionStatus_Response.parseFrom(decode);
            String jsonString = JsonFormat.printer().print(pollAuthSessionStatusResponse);
            System.out.println("123123 : "+jsonString);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

        System.out.println("123123");
    }



}
