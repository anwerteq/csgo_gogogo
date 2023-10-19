package com.chenerzhu.crawler.proxy.util;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.RecaptchaUtil.Recaptchav2Root;
import com.chenerzhu.crawler.proxy.util.RecaptchaUtil.Task;

import java.util.HashMap;

public class Recaptchav2Util {

    //{"errorId":0,"taskId":74804962490}
    public static String checkRecaptchav2(String websiteKey) {
        Recaptchav2Root recaptchav2Root = Recaptchav2Root.getRecaptchav2Root();
        Task task = recaptchav2Root.getTask();
        task.setWebsiteKey(websiteKey);
        String url = "https://api.2captcha.com/createTask";
        String tastRes = HttpClientUtils.sendPost(url, JSONObject.toJSONString(recaptchav2Root), new HashMap<>());
        String taskId = JSONObject.parseObject(tastRes).getString("taskId");
        int count = 0;
        while (count++ < 8) {
            SleepUtil.sleep(7000);
            String res = getRes(taskId);

            if ("CAPCHA_NOT_READY".equals(res)){
                System.out.println("createTask是否准备：" + res);
                continue;
            }
            String captcha_text = res.split("\\|")[1];
            return captcha_text;
        }
        return "";
    }


    public static String getRes(String id) {
        String url = "https://2captcha.com/res.php?key=f7a920a4720b17f65b77c325d6c0f0f7" +
                "&action=get&sandbox=1&id=" + id;
        String reponses = HttpClientUtils.sendGet(url, new HashMap<>());
        return reponses;
    }
}
