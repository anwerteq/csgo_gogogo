package com.chenerzhu.crawler.proxy.util.steamlogin;


import lombok.Data;

@Data
public class SteamUserDate {

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

}
