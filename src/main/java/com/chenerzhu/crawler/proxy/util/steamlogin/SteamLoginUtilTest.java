package com.chenerzhu.crawler.proxy.util.steamlogin;


import com.chenerzhu.crawler.proxy.protobufs.CAuthenticationGetPasswordRSAPublicKeyResponse;
import com.chenerzhu.crawler.proxy.protobufs.CAuthentication_GetPasswordRSAPublicKey_Request;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SteamLoginUtilTest {


    public static void main2(String[] args) {
        try {
            String str = "CoAEYmUzOGJkM2E3YjgxNjYwZTQ2ZjZjNzFjYmUzZjM1MGE0ZDA1Mjc5YjM2MzM1NTJjODg1ZTE3NGU0ZjUyYjQ2MjRiYTI1MmVlMmQ2OTQyNWE0MTgyMmQyMzA0OTNkMWU0M2NiMWRlNTdmM2RjNWJjYWY3YzM5MjliZjVhNTQyNjI4ZjkxY2ZkMTAzYTFhZjNlYjVkYmM0Y2MxODlmYjIwZjBlYmYzNTFhYWI4MzMzZTMxZjI5YjRhMWVmNTVlMWVkZDNhMjJjZDVmZGRhNmYyNjllMTVjN2Q5NmI3MTVlMmNjZGVhNDYxNWNjN2Q1YjliNjExNjAwNmYxNTg2MjQzZjJhYTA0MzAyNGM1NzIzMGU2MzI0NGI0OGEyZmUxZjg0ODA5ZTc0MTMyYTQzNDY5ZmU1M2U3NmI2MDJkYzJlM2Y1ODdiZDA3YTQ5YzRiYTk5NzIwMjNiNTFjZWFhYzc3NDFiZGQ0Mzc1ZTQwY2EyZTY2MGU2MGUzMDgxZmZiYjYzNmJjMDE3NGU4ZDZlMGFiMWNmNDFkOWM3MWZkYTUxZjA0ZDcyZjdlNjEyZjBhMDcyM2YwOTlhMGVjNzEyNTU4YTkyMDVlZTA1MTFlMGMyZDY0MmViMmRjNTU4YmYwYTdkNjgyY2UwNThlNzczNzZkMjBjYjkyMWI0M2YwNmE0NWYSBjAxMDAwMRiQtdb3yxA=";
            byte[] decode = Base64.getDecoder().decode(str);
            byte[] bytes = "�\u0004be38bd3a7b81660e46f6c71cbe3f350a4d05279b3633552c885e174e4f52b4624ba252ee2d69425a41822d230493d1e43cb1de57f3dc5bcaf7c3929bf5a542628f91cfd103a1af3eb5dbc4cc189fb20f0ebf351aab8333e31f29b4a1ef55e1edd3a22cd5fdda6f269e15c7d96b715e2ccdea4615cc7d5b9b6116006f1586243f2aa043024c57230e63244b48a2fe1f84809e74132a43469fe53e76b602dc2e3f587bd07a49c4ba9972023b51ceaac7741bdd4375e40ca2e660e60e3081ffbb636bc0174e8d6e0ab1cf41d9c71fda51f04d72f7e612f0a0723f099a0ec712558a9205ee0511e0c2d642eb2dc558bf0a7d682ce058e77376d20cb921b43f06a45f\u0012\u0006010001\u0018�����\u0010".getBytes();
            CAuthenticationGetPasswordRSAPublicKeyResponse.CAuthentication_GetPasswordRSAPublicKey_Response getPasswordRSAPublicKeyResponse = CAuthenticationGetPasswordRSAPublicKeyResponse.
                    CAuthentication_GetPasswordRSAPublicKey_Response.parseFrom(decode);
            System.out.println("Public Key: " + getPasswordRSAPublicKeyResponse.getPublickeyExp());
        }  catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws UnsupportedEncodingException {
        Http Http = new Http();
        CAuthentication_GetPasswordRSAPublicKey_Request.GetPasswordRSAPublicKey_Request accountProtobufs = CAuthentication_GetPasswordRSAPublicKey_Request.GetPasswordRSAPublicKey_Request.newBuilder().setAccountName("mu64kkro").build();


        // 序列化消息
        byte[] serializedMessage = accountProtobufs.toByteArray();

        // Base64 编码
        String base64EncodedMessage = Base64.getEncoder().encodeToString(serializedMessage);

        // 打印 Base64 编码后的消息
        System.out.println("Base64 Encoded Message: " + base64EncodedMessage);

        // 序列化为字节数组
        Map<String, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("input_protobuf_encoded", base64EncodedMessage);

        HttpBean get = Http.request("https://api.steampowered.com/IAuthenticationService/GetPasswordRSAPublicKey/v1", "GET", objectObjectHashMap, "", true,
                "https://steamcommunity.com", false);
        byte[] bytes = get.getResponse().getBytes();
        try {
            CAuthenticationGetPasswordRSAPublicKeyResponse.CAuthentication_GetPasswordRSAPublicKey_Response getPasswordRSAPublicKeyResponse = CAuthenticationGetPasswordRSAPublicKeyResponse.
                    CAuthentication_GetPasswordRSAPublicKey_Response.parseFrom("\\n\\x80\\x04b03f187d58054f10f4f5efaa28e4da8c777910f701f5bed185b71e600e5fa74a79cea395c943cbd39404bf55fa507144b302032a5b3857e41b86cbb6901224c8e79ccbe79902bb40f027581893f3ef7d1902f811442f635fe8891d96cf25f486fdd0a94457f8b17169d27d167c93af2d4ff669e90d62695173c6".getBytes());
            System.out.println("Public Key: " + getPasswordRSAPublicKeyResponse.getPublickeyExp());
        }  catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    // 辅助函数：将字节数组转换为十六进制字符串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
