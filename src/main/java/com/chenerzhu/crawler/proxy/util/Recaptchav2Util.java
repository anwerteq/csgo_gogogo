package com.chenerzhu.crawler.proxy.util;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.RecaptchaUtil.Recaptchav2Root;
import com.chenerzhu.crawler.proxy.util.RecaptchaUtil.Task;

import java.util.HashMap;
import java.util.Map;

public class Recaptchav2Util {

    //{"errorId":0,"taskId":74804962490}
    public static String checkRecaptchav2(String websiteKey) {
        Recaptchav2Root recaptchav2Root = Recaptchav2Root.getRecaptchav2Root();
        Task task = recaptchav2Root.getTask();
        task.setWebsiteKey(websiteKey);
        String url = "https://api.2captcha.com/createTask";
        String tastRes = HttpClientUtils.sendPost(url, JSONObject.toJSONString(recaptchav2Root), new HashMap<>());
        String taskId = JSONObject.parseObject(tastRes).getString("taskId");
        String res = getRes(taskId);
        return res;
    }

    public static  String getText(String Base64){
        String url = "https://2captcha.com/in.php?sandbox=1";
        Map<String, String> whereMap = new HashMap<>();
        whereMap.put("key","f7a920a4720b17f65b77c325d6c0f0f7");
        whereMap.put("method","audio");
        whereMap.put("body",Base64);
        whereMap.put("lang","en");
        whereMap.put("json","1");
        String tastRes = HttpClientUtils.sendPost(url, JSONObject.toJSONString(whereMap), new HashMap<>());
        String taskId = JSONObject.parseObject(tastRes).getString("request");
        String res = getRes(taskId);
        return res;
    }


    public static String getRes(String id) {
        String url = "https://2captcha.com/res.php?key=f7a920a4720b17f65b77c325d6c0f0f7" +
                "&action=get&sandbox=1&id=" + id;
        int count = 0;
        String reponses = "";
        while (count++ < 8) {
            SleepUtil.sleep(7000);
            reponses = HttpClientUtils.sendGet(url, new HashMap<>());
            if ("CAPCHA_NOT_READY".equals(reponses)){
                System.out.println("createTask是否准备：" + reponses);
                continue;
            }
            String captcha_text = reponses.split("\\|")[1];
            return captcha_text;
        }


        return reponses;
    }
}
