package com.xiaojuzi.util.steamlogin;

import lombok.Data;

@Data
public class Session {

    private String SteamID;
    private String AccessToken;
    private String RefreshToken;
    private String SessionID = "";
}
