package com.chenerzhu.crawler.proxy.util.steamlogin;


import com.chenerzhu.crawler.proxy.protobufs.CAuthenticationGetPasswordRSAPublicKeyResponse;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SteamLoginUtilTest {

    public static void main(String[] args) throws UnsupportedEncodingException {
        Http Http = new Http();
        CAuthenticationGetPasswordRSAPublicKeyResponse.GetPasswordRSAPublicKey_Request accountProtobufs = CAuthenticationGetPasswordRSAPublicKeyResponse.GetPasswordRSAPublicKey_Request.newBuilder().setAccountName("z24970087883").build();

        String base64EncodedMessage = Base64.getEncoder().encodeToString(accountProtobufs.toByteArray());

        // 序列化为字节数组
        Map<String, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("input_protobuf_encoded", base64EncodedMessage);

        HttpBean get = Http.request("https://steamcommunity.com/login/getrsakey", "GET", objectObjectHashMap, "", true,
                "https://steamcommunity.com", false);
//        CAuthenticationGetPasswordRSAPublicKeyResponse.GetPasswordRSAPublicKey_Request getPasswordRSAPublicKeyRequest = CAuthenticationGetPasswordRSAPublicKeyResponse.GetPasswordRSAPublicKey_Request.parseFrom(get.getResponse());
        System.out.println(get);
    }
}
