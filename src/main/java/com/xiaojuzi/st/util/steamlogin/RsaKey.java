package com.xiaojuzi.st.util.steamlogin;

/**
 * Created by Mr.W on 2017/5/23.
 * rsa key 实例
 */
public class RsaKey {

    private boolean success;

    private String publickey_mod;

    private String publickey_exp;

    private String timestamp;

    private String token_gid;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPublickey_mod() {
        return publickey_mod;
    }

    public void setPublickey_mod(String publickey_mod) {
        this.publickey_mod = publickey_mod;
    }

    public String getPublickey_exp() {
        return publickey_exp;
    }

    public void setPublickey_exp(String publickey_exp) {
        this.publickey_exp = publickey_exp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken_gid() {
        return token_gid;
    }

    public void setToken_gid(String token_gid) {
        this.token_gid = token_gid;
    }
}
