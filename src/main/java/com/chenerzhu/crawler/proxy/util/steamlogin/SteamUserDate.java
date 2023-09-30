package com.chenerzhu.crawler.proxy.util.steamlogin;


import lombok.Data;

@Data
public class SteamUserDate {

    private String shared_secret;
    private String serial_number;
    private String revocation_code;
    private String uri;
    private String userPsw;
    private StringBuilder cookies;
    private long server_time;
    private String account_name;
    private String token_gid;
    private String identity_secret;
    private String secret_1;
    private int status;
    private String device_id;
    private boolean fully_enrolled;
    private Session Session = new Session();

}


/**
 * Auto-generated: 2023-10-01 3:1:5
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
class Session {

    private long SteamID;
    private String AccessToken;
    private String RefreshToken;
    private String SessionID;
}
