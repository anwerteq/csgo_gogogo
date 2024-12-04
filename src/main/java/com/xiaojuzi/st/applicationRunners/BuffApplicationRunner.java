package com.xiaojuzi.st.applicationRunners;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.xiaojuzi.st.buff.BuffUserData;
import com.xiaojuzi.st.cache.BuffCacheService;
import com.xiaojuzi.st.steam.util.SleepUtil;
import com.xiaojuzi.st.util.HttpClientUtils;
import com.xiaojuzi.st.util.bufflogin.BuffAccountInfoConfig;
import com.xiaojuzi.st.util.bufflogin.BuffAutoLoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 启动后buff账号信息初始化
 */
@Slf4j
@Component
@Order(2)
public class BuffApplicationRunner implements ApplicationRunner {

    public static List<BuffUserData> buffUserDataList = new ArrayList<>();

    public static ThreadLocal<BuffUserData> buffUserDataThreadLocal = new ThreadLocal<>();

    public static Double cny = 0.0;


    @Autowired
    BuffAutoLoginUtil buffAutoLoginUtil;

    @Autowired
    BuffCacheService buffCacheService;
    @Autowired
    private BuffAccountInfoConfig buffAccountInfoConfig;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (true){
            return;
        }
        List<String> buff_cookies = buffAccountInfoConfig.getBuff_cookies();
        List<String> account_information = buffAccountInfoConfig.getAccount_information();
        if (CollectionUtil.isEmpty(account_information) && CollectionUtil.isEmpty(buff_cookies)) {
            log.error("未加载到buff账号，请检查[buff.account_information]配置");
            SleepUtil.sleep(5000);
            return;
        }

        for (String acountData : account_information) {
            BuffUserData buffUserData = new BuffUserData();
            String acount = acountData.split("-")[0];
            String pwd = acountData.split("-")[1];
            buffUserData.setAcount(acount);
            buffUserData.setPwd(pwd);
            int count = 0;
            String cookie = "";
            while (StrUtil.isEmpty(cookie) && count++ < 3) {
                cookie = buffCacheService.getCookie(acount, buffUserData);
                break;
            }
            //获取steamid
            String steamId = buffCacheService.getSteamId(acount, cookie);
            buffUserData.setSteamId(steamId);
            buffUserDataList.add(buffUserData);
        }


        if (CollectionUtil.isNotEmpty(buff_cookies)) {
            for (int i = 1; i <= buff_cookies.size(); i++) {
                String sesionId = buff_cookies.get(i - 1);
                BuffUserData buffUserData = new BuffUserData();
                buffUserData.setAcount("手动填入的cookie:" + i);
                buffUserData.setCookie(sesionId);
                String cookie = buffAutoLoginUtil.getCookie(buffUserData);
                if (null == cookie) {
                    continue;
                }
                buffUserDataList.add(buffUserData);
            }
        }
        for (BuffUserData buffUserData : buffUserDataList) {
            log.info("加载buff账号：{}成功", buffUserData.getAcount());
        }
    }


    /**
     * 查询汇率
     *
     * @return
     */
    public Double latestUSD() {
        String url = "https://api.exchangerate-api.com/v4/latest/USD";
        String sendGet = HttpClientUtils.sendGet(url, new HashMap<>());
        JSONObject jsonObject = JSONObject.parseObject(sendGet);
        String CNYStr = jsonObject.getJSONObject("rates").getString("CNY");
        cny = Double.valueOf(CNYStr);
        return 0.0;
    }
}
