package com.chenerzhu.crawler.proxy.util.steamlogin;


import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.protobufs.CAuthenticationBeginAuthSessionViaCredentialsRequest;
import com.chenerzhu.crawler.proxy.protobufs.CAuthenticationBeginAuthSessionViaCredentialsResponse;
import com.chenerzhu.crawler.proxy.protobufs.CAuthenticationGetPasswordRSAPublicKeyResponse;
import com.chenerzhu.crawler.proxy.protobufs.CAuthentication_GetPasswordRSAPublicKey_Request;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ByteString;
import javax.crypto.Cipher;
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

    public static void main(String[] args) {
        String basestr= "ClJNb3ppbGxhLzUuMCAoWDExOyBMaW51eCB4ODZfNjQ7IHJ2OjEuOS41LjIwKSBHZWNrby8yODEyLTEyLTEwIDA0OjU2OjI4IEZpcmVmb3gvMy44EghtdTY0a2tybxrYAm5iNTNoNW92L3ZkMlJndXUrR2JjUVZneVhpUXhpRlVDaEhTRzR0RmpnTEpLbXZ4eE5ZeHNuNHVUL245bkpncUpJU0ZWbmZKeUdnUWJGUHpLY21qSENHWlEzRUhZNXlHSFFIcFlQSDYwSlNack5aUHdqYnF2L1ZLN0xjdmVYRXErVk5waW44SjQzd3VEUXlRRTdWbStGU0pCampLd21vRFZRSCt4UmJtUi9BNGpPeEMrVmFXSUo4QWI4b0tmN2djd1ZYbmNHa3N0bnRxcU16djV4SUJxaUJocU9aekZsVmN6VEZmVTl5VzRWS1pQN2U2bzg0S2FpRW8rYWYrcFNQbTZzZERJaUM4VHpBN1drM2JQbnc0cGt2blhsTEZNcXUwTEdkNko3QzI4dkY0clNFOXIyWFRzVU1ubmM4WEtuVWdDNUVGdStlVGdqTzgxQ2p0ZjNYcU1nUT09IJDK5suVASgBMAM4AUIJQ29tbXVuaXR5";
        byte[] decode = Base64.getDecoder().decode(basestr);
        try {
            CAuthenticationBeginAuthSessionViaCredentialsRequest.CAuthentication_BeginAuthSessionViaCredentials_Request cAuthenticationBeginAuthSessionViaCredentialsRequest = CAuthenticationBeginAuthSessionViaCredentialsRequest.CAuthentication_BeginAuthSessionViaCredentials_Request.parseFrom(decode);
            System.out.println("123123");
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        System.out.println("123123");
    }

    public static void main1(String[] args) throws UnsupportedEncodingException {
        steamLoginUrlFlag.set(true);
        CAuthenticationGetPasswordRSAPublicKeyResponse.CAuthentication_GetPasswordRSAPublicKey_Response step2Value = step2();
        String encryptPasswordProtobuf = encryptPasswordProtobuf(step2Value.getPublickeyExp(), step2Value.getPublickeyMod(), "QingLiu98!");
        step3("mu64kkro",encryptPasswordProtobuf,Long.valueOf(step2Value.getTimestamp()));
        // CNnRl9QWT9leRnGksIuwzJBLzfzX9OFKGNMzl+sXOhWUvlFt0dN1xfwD5Hs0Isq/QOSCRIomOcCNKoodByZzwP1UzIOxmqtsA2fHv/2E0gzFLmy5cAbBKq4eQE5MgS2p03ALr5GIWv5tMrFSec+o2rr+aXn9yqYFzY8OfFdOK+/xgNM07Nwpx1rRViTCZVn1d3wtRMNqPwyCTDRLleM4EC9DHJyK3XaKGICa1aBXMvMLOGMJEHa8wwWCNoUaBB5s

    }

    public static void step1() throws UnsupportedEncodingException {
        String url = "https://steamcommunity.com";
        Http Http = new Http();
        HttpBean get = Http.request(url, "GET", new HashMap<>(), "", true,
                "https://steamcommunity.com", false);
        String response = get.getResponse();

    }

    /**
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public static CAuthenticationGetPasswordRSAPublicKeyResponse.CAuthentication_GetPasswordRSAPublicKey_Response step2() throws UnsupportedEncodingException {
        String url = "https://api.steampowered.com/IAuthenticationService/GetPasswordRSAPublicKey/v1";
        CAuthentication_GetPasswordRSAPublicKey_Request.GetPasswordRSAPublicKey_Request accountProtobufs = CAuthentication_GetPasswordRSAPublicKey_Request
                .GetPasswordRSAPublicKey_Request.newBuilder().setAccountName("mu64kkro").build();
        steamLoginUrlFlag.set(true);
        // Base64 编码
        String base64EncodedMessage = Base64.getEncoder().encodeToString( accountProtobufs.toByteArray());
        // 序列化为字节数组
        Map<String, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("input_protobuf_encoded", base64EncodedMessage);
        Map<String, String> headerMap = new HashMap() {{
            put("Referer", "https://steamcommunity.com");
        }};
        String response = HttpClientUtils.sendGet(url, headerMap, objectObjectHashMap);
        byte[] decode = Base64.getDecoder().decode(response);
        CAuthenticationGetPasswordRSAPublicKeyResponse.CAuthentication_GetPasswordRSAPublicKey_Response getPasswordRSAPublicKeyResponse = null;
        try {
            getPasswordRSAPublicKeyResponse = CAuthenticationGetPasswordRSAPublicKeyResponse.
                    CAuthentication_GetPasswordRSAPublicKey_Response.parseFrom(decode);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        return getPasswordRSAPublicKeyResponse;
    }

    public static void step3(String account_name,String encrypted_password,Long rsa_timestamp){

        CAuthenticationBeginAuthSessionViaCredentialsRequest.CAuthentication_BeginAuthSessionViaCredentials_Request build = CAuthenticationBeginAuthSessionViaCredentialsRequest.CAuthentication_BeginAuthSessionViaCredentials_Request
                .newBuilder()
                .setAccountName(account_name)
//                .setEncryptedPassword(ByteString.copyFromUtf8(encrypted_password))
                .setEncryptionTimestamp(rsa_timestamp)
                .setRememberLogin(true)
                .setPlatformType(CAuthenticationBeginAuthSessionViaCredentialsRequest.EAuthTokenPlatformType.k_EAuthTokenPlatformType_MobileApp)
                .setWebsiteId("'Community'")
                .setPersistence(CAuthenticationBeginAuthSessionViaCredentialsRequest.ESessionPersistence.k_ESessionPersistence_Persistent)
                .setDeviceFriendlyName("Mozilla/5.0 (X11; Linux x86_64; rv:1.9.5.20) Gecko/2812-12-10 04:56:28 Firefox/3.8")
                .build();
        String url ="https://api.steampowered.com/IAuthenticationService/BeginAuthSessionViaCredentials/v1";
        // Base64 编码
        String base64EncodedMessage = Base64.getEncoder().encodeToString(build.toByteArray());
        // 序列化为字节数组
        Map<String, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("input_protobuf_encoded", base64EncodedMessage);
        Map<String, String> headerMap = new HashMap() {{
            put("Referer", "https://steamcommunity.com");
        }};
        String response = HttpClientUtils.sendPost(url, JSONObject.toJSONString(objectObjectHashMap), headerMap);

        byte[] decode = Base64.getDecoder().decode(response);
        CAuthenticationBeginAuthSessionViaCredentialsResponse.ClientResponse clientResponse = null;
        try {
             clientResponse = CAuthenticationBeginAuthSessionViaCredentialsResponse.ClientResponse.parseFrom(decode);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        System.out.println("12312");
    }

    /**
     * steam登录需对密码加密
     * @param publickeyExp
     * @param publickeyMod
     * @return
     * @throws Exception
     */
    public static String encryptPasswordProtobuf(String  publickeyExp,String publickeyMod,String password)  {
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
        }catch (Exception e){
            throw  new RuntimeException("encryptPasswordProtobuf error",e);
        }
    }

}
