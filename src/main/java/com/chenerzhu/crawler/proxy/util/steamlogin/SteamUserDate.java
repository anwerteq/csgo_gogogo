package com.chenerzhu.crawler.proxy.util.steamlogin;


import in.dragonbra.javasteam.steam.authentication.AccessTokenGenerateResult;
import in.dragonbra.javasteam.steam.authentication.SteamAuthentication;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOnCallback;
import in.dragonbra.javasteam.steam.steamclient.callbacks.ConnectedCallback;
import in.dragonbra.javasteam.util.Strings;
import lombok.Data;

import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class SteamUserDate {

    /**
     * 保存steamtoken信息
     */
    @Transient
    public static Map<String,String> steamTokensNumberAndTokenMap = new ConcurrentHashMap<>();

    private String shared_secret;
    private String serial_number;
    private String revocation_code;
    private String uri;
    private String userPsw;
    private StringBuilder cookies = new StringBuilder();
    private long server_time;
    private String account_name;
    private String token_gid;
    private String identity_secret;
    private String secret_1;
    private int status;
    private String device_id;
    private boolean fully_enrolled;
    private String apikey;
    private Session Session = new Session();
    private String oneTimeCode;

    @Transient
    private SteamAuthentication auth;

    @Transient
    private LoggedOnCallback callback;

    public void refreshCookies(Map<String,String> map ){
        String string = cookies.toString();
        Map<String,String> objectObjectHashMap = new HashMap<>();
        String[] values = string.split(";");
        for (String value : values) {
            String[] split = value.split("=");
            objectObjectHashMap.put(split[0], split[1]);
        }
        objectObjectHashMap.putAll(map);
        StringJoiner sj = new StringJoiner(";");
        for (Map.Entry<String, String> entry : objectObjectHashMap.entrySet()) {
            sj.add(entry.getKey() + "=" + entry.getValue());
        }
        setCookies(new StringBuilder(sj.toString()));


    }

    public StringBuilder getCookies() {
        if (auth != null) {
            AccessTokenGenerateResult newTokens = auth.generateAccessTokenForApp(callback.getClientSteamID(), getSession().getRefreshToken(), true);
            String  accessToken = newTokens.getAccessToken();
            if (!Strings.isNullOrEmpty(newTokens.getRefreshToken())) {
                String refreshToken = newTokens.getRefreshToken();
                getSession().setRefreshToken(refreshToken);
            }
            String steamLoginSecure = callback.getClientSteamID().convertToUInt64() + "||" + accessToken;
            Map<String,String> map  = new HashMap<>();
            map.put("steamLoginSecure", steamLoginSecure);
            refreshCookies(map);
        }
        return cookies;
    }


    @Override
    public String toString() {
        return "SteamUserDate{" +
                "shared_secret='" + shared_secret + '\'' +
                ", serial_number='" + serial_number + '\'' +
                ", revocation_code='" + revocation_code + '\'' +
                ", uri='" + uri + '\'' +
                ", userPsw='" + userPsw + '\'' +
                ", cookies=" + cookies +
                ", server_time=" + server_time +
                ", account_name='" + account_name + '\'' +
                ", token_gid='" + token_gid + '\'' +
                ", identity_secret='" + identity_secret + '\'' +
                ", secret_1='" + secret_1 + '\'' +
                ", status=" + status +
                ", device_id='" + device_id + '\'' +
                ", fully_enrolled=" + fully_enrolled +
                ", apikey='" + apikey + '\'' +
                ", Session=" + Session +
                ", oneTimeCode='" + oneTimeCode + '\'' +
                ", auth=" + auth +
                ", callback=" + callback +
                '}';
    }
}
