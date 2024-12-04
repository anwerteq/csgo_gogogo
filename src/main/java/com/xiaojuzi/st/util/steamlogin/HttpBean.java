package com.xiaojuzi.st.util.steamlogin;

/**
 * Created by Mr.W on 2017/6/28.
 * http返回结果
 */
public class HttpBean {
    private String cookies;
    private String response;
    private int code;
    private String session;
    private boolean timeOut;
    private boolean sessionInvalid;

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isTimeOut() {
        return timeOut;
    }

    public void setTimeOut(boolean timeOut) {
        this.timeOut = timeOut;
    }

    public boolean isSessionInvalid() {
        return sessionInvalid;
    }

    public void setSessionInvalid(boolean sessionInvalid) {
        this.sessionInvalid = sessionInvalid;
    }
}
