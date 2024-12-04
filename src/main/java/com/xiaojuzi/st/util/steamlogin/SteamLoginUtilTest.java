package com.xiaojuzi.st.util.steamlogin;


import com.alibaba.fastjson.JSONObject;
import com.xiaojuzi.st.applicationRunners.SteamApplicationRunner;
import com.xiaojuzi.st.config.CookiesConfig;
import com.xiaojuzi.st.util.HttpClientUtils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class SteamLoginUtilTest {

    // steam登录标识
    public static ThreadLocal<Boolean> steamLoginUrlFlag = new ThreadLocal<>();

    // steam登录的的请求头数据
    public static ThreadLocal<Map<String, String>> steamLoginHeaderMapThreadLocal = new ThreadLocal<>();
    // 记录服务器时间
    public static long timeDelta = Long.MAX_VALUE;
    static {
        steamLoginHeaderMapThreadLocal.set(new HashMap<>());
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
//        SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Response step2Value = step2();
//        String encryptPasswordProtobuf = encryptPasswordProtobuf(step2Value.getPublickeyExp(), step2Value.getPublickeyMod(), steamUserDate.getUserPsw());
//        SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response authSessionViaCredentialsResponse =
//                step3(steamUserDate.getAccount_name(), encryptPasswordProtobuf, Long.valueOf(step2Value.getTimestamp()));
//        SteammessagesAuth.CAuthentication_AllowedConfirmation allowedConfirmations = authSessionViaCredentialsResponse.getAllowedConfirmations(0);
//        if (allowedConfirmations != null) {
//            //移动设备
//            SteammessagesAuth.EAuthSessionGuardType confirmationType = allowedConfirmations.getConfirmationType();
//            if (confirmationType.getNumber() == k_EAuthSessionGuardType_DeviceCode.getNumber()) {
//                if (StrUtil.isEmpty(steamUserDate.getShared_secret())) {
//                    //获取shared_secret值
//                } else {
//                    String one_time_code = generateOneTimeCode(steamUserDate.getShared_secret(), null);
//                    steamUserDate.setOneTimeCode(one_time_code);
//                    step4(authSessionViaCredentialsResponse);
//                    step5(authSessionViaCredentialsResponse);
//                    System.out.println("123123");
//                }
//            }
//        }

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
        // Pack the timestamp as Big Endian, uint64
        ByteBuffer timeBuffer = ByteBuffer.allocate(8);
        timeBuffer.order(ByteOrder.BIG_ENDIAN);
        timeBuffer.putLong(timestamp / 30);

        // Calculate HMAC using SHA-1
        Mac hmac = null;
        try {
            hmac = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(sharedSecret), "HmacSHA1");
        try {
            hmac.init(keySpec);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        byte[] timeHmac = hmac.doFinal(timeBuffer.array());

        // Get the 20th byte and compute the offset
        int begin = timeHmac[19] & 0xf;

        // Unpack as Big Endian uint32
        ByteBuffer codeBuffer = ByteBuffer.wrap(timeHmac, begin, 4);
        codeBuffer.order(ByteOrder.BIG_ENDIAN);
        int fullCode = codeBuffer.getInt() & 0x7fffffff;

        // Define the character set
        String chars = "23456789BCDFGHJKMNPQRTVWXY";
        StringBuilder code = new StringBuilder();

        // Generate the code
        for (int i = 0; i < 5; i++) {
            int[] divmod = divmod(fullCode, chars.length());
            fullCode = divmod[0];
            int index = divmod[1];
            code.append(chars.charAt(index));
        }

        return code.toString();
    }
    public static int[] divmod(int a, int b) {
        return new int[]{a / b, a % b};
    }

    private static long tryToGetTimeDeltaFromSteam() {
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
        return timeDelta;
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
        headerMap.put("Content-Type", "application/x-www-form-urlencoded"); // 设置请求头为 JSON
        headerMap.remove("Content-Length");
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        String response = HttpClientUtils.sendPost(url, "", headerMap);
        JSONObject jsonObject = JSONObject.parseObject(response);
        Long server_time = jsonObject.getJSONObject("response").getLong("server_time");
        return server_time;
    }

    public static void step1() throws UnsupportedEncodingException {
        tryToGetTimeDeltaFromSteam();
        Map<String, String> headerMap = SteamLoginUtilTest.steamLoginHeaderMapThreadLocal.get();
        headerMap.put("Referer", "https://steamcommunity.com");
        headerMap.put("Cookie", CookiesConfig.steamCookies.get());
        headerMap.put("Content-Type", "application/x-www-form-urlencoded"); // 设置请求头为 JSON
        headerMap.remove("Content-Length");
        String url = "https://steamcommunity.com";
        String response = HttpClientUtils.sendGet(url, headerMap, new HashMap<>());
    }


//    /**
//     * @return
//     * @throws UnsupportedEncodingException
//     */
//    public static SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Response step2() throws UnsupportedEncodingException {
//        String url = "https://api.steampowered.com/IAuthenticationService/GetPasswordRSAPublicKey/v1";
//        SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Request accountProtobufs = SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Request.newBuilder().setAccountName("mu64kkro").build();
//        // Base64 编码
//        String base64EncodedMessage = Base64.getEncoder().encodeToString(accountProtobufs.toByteArray());
//        // 序列化为字节数组
//        Map<String, String> objectObjectHashMap = new HashMap<>();
//        objectObjectHashMap.put("input_protobuf_encoded", base64EncodedMessage);
//        Map<String, String> headerMap = SteamLoginUtilTest.steamLoginHeaderMapThreadLocal.get();
//        headerMap.put("Referer", "https://steamcommunity.com");
//        headerMap.put("Cookie", CookiesConfig.steamCookies.get());
//        String response = HttpClientUtils.sendGet(url, headerMap, objectObjectHashMap);
//        byte[] decode = Base64.getDecoder().decode(response);
//        SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Response rsaPublicKeyResponse = null;
//        try {
//            rsaPublicKeyResponse = SteammessagesAuth.CAuthentication_GetPasswordRSAPublicKey_Response.parseFrom(decode);
//            String jsonString = JsonFormat.printer().print(rsaPublicKeyResponse);
//            System.out.println(rsaPublicKeyResponse);
//        } catch (InvalidProtocolBufferException e) {
//            throw new RuntimeException(e);
//        }
//        return rsaPublicKeyResponse;
//    }
//
//    public static SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response step3(String account_name, String encrypted_password, Long rsa_timestamp) {
//
//        SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Request credentialsRequest = SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Request
//                .newBuilder()
//                .setAccountName(account_name)
//                .setEncryptedPassword(encrypted_password)
//                .setEncryptionTimestamp(rsa_timestamp)
//                .setRememberLogin(true)
//                .setPlatformType(SteammessagesAuth.EAuthTokenPlatformType.k_EAuthTokenPlatformType_MobileApp)
//                .setWebsiteId("Community")
//                .setPersistence(SteammessagesAuth.ESessionPersistence.k_ESessionPersistence_Persistent)
//                .setDeviceFriendlyName("Mozilla/5.0 (X11; Linux x86_64; rv:1.9.5.20) Gecko/2812-12-10 04:56:28 Firefox/3.8")
//                .build();
//        String url = "https://api.steampowered.com/IAuthenticationService/BeginAuthSessionViaCredentials/v1";
//        // Base64 编码
//        String base64EncodedMessage = Base64.getEncoder().encodeToString(credentialsRequest.toByteArray());
//        // 序列化为字节数组
//        Map<String, String> objectObjectHashMap = new HashMap<>();
//        objectObjectHashMap.put("input_protobuf_encoded", base64EncodedMessage);
//        Map<String, String> headerMap = SteamLoginUtilTest.steamLoginHeaderMapThreadLocal.get();
//        headerMap.put("Origin", "https://steamcommunity.com");
//        headerMap.put("Referer", "https://steamcommunity.com");
//        headerMap.put("Cookie", CookiesConfig.steamCookies.get());
//        headerMap.put("Content-Type", "application/x-www-form-urlencoded"); // 设置请求头为 JSON
//        headerMap.put("Accept-Encoding", "gzip, deflate");
//        headerMap.put("User-Agent", "python-requests/2.32.3");
//
//        String response = HttpClientUtils.sendPostForm(url, "", headerMap, objectObjectHashMap);
//// {'User-Agent': 'python-requests/2.32.3', 'Accept-Encoding': 'gzip, deflate', 'Accept': '*/*', 'Connection': 'keep-alive', 'Referer': 'https://steamcommunity.com/', 'Origin': 'https://steamcommunity.com', 'Content-Length': '645', 'Content-Type': 'application/x-www-form-urlencoded'}
//        byte[] decode = Base64.getDecoder().decode(response);
//        try {
//            SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response beginAuthSessionViaCredentialsResponse = SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response.parseFrom(decode);
//            System.out.println("12312");
//            return beginAuthSessionViaCredentialsResponse;
//
//        } catch (InvalidProtocolBufferException e) {
//            throw new RuntimeException(e);
//        }
//    }

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

//    public static  void step4(SteammessagesAuth.CAuthentication_BeginAuthSessionViaCredentials_Response authSessionViaCredentialsResponse){
//        SteamUserDate steamUserDate = SteamApplicationRunner.steamUserDateTL.get();
//        Map<String, String> headerMap = SteamLoginUtilTest.steamLoginHeaderMapThreadLocal.get();
//        headerMap.put("Cookie", CookiesConfig.steamCookies.get());
//        headerMap.put("Content-Type", "application/x-www-form-urlencoded"); // 设置请求头为 JSON
//        headerMap.put("Origin", "https://steamcommunity.com");
//        headerMap.put("Referer", "https://steamcommunity.com");
//        headerMap.remove("Content-Length");
//        long clineId = authSessionViaCredentialsResponse.getClientId();
//        SteammessagesAuth.CAuthentication_UpdateAuthSessionWithSteamGuardCode_Request build = SteammessagesAuth.CAuthentication_UpdateAuthSessionWithSteamGuardCode_Request.newBuilder()
//                .setClientId(authSessionViaCredentialsResponse.getClientId())
//                .setCode(steamUserDate.getOneTimeCode())
//                .setSteamid(Long.valueOf(steamUserDate.getSession().getSteamID()))
//                .setCodeTypeValue(k_EAuthSessionGuardType_DeviceCode.getNumber()).build();
//        String base64EncodedMessage = Base64.getEncoder().encodeToString(build.toByteArray());
//        Map<String, String> paraMap = new HashMap<>();
//        paraMap.put("input_protobuf_encoded",base64EncodedMessage);
//        String url = "https://api.steampowered.com/IAuthenticationService/UpdateAuthSessionWithSteamGuardCode/v1";
//        String response = HttpClientUtils.sendPostForm(url, "",headerMap,paraMap);
//        if (!"".equals(response)){
//            throw  new RuntimeException("执行报错");
//        }
//        System.out.println("!@#");
//    }




}
