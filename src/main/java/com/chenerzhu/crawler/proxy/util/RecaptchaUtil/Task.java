package com.chenerzhu.crawler.proxy.util.RecaptchaUtil;

public class Task {
    private String type = "RecaptchaV2TaskProxyless";

    private String websiteURL = "https://store.steampowered.com/join";

    private String websiteKey;

    private boolean isInvisible = false;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    public String getWebsiteURL() {
        return this.websiteURL;
    }

    public void setWebsiteKey(String websiteKey) {
        this.websiteKey = websiteKey;
    }

    public String getWebsiteKey() {
        return this.websiteKey;
    }

    public void setIsInvisible(boolean isInvisible) {
        this.isInvisible = isInvisible;
    }

    public boolean getIsInvisible() {
        return this.isInvisible;
    }
}
