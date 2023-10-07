package com.chenerzhu.crawler.proxy.applicationRunners;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.cache.BuffCacheService;
import com.chenerzhu.crawler.proxy.util.bufflogin.BuffAccountInfoConfig;
import com.chenerzhu.crawler.proxy.util.bufflogin.BuffAutoLoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 启动后buff账号信息初始化
 */
@Slf4j
@Component
public class BuffApplicationRunner implements ApplicationRunner {

    public static List<BuffUserData> buffUserDataList = new ArrayList<>();
    @Autowired
    BuffAutoLoginUtil buffAutoLoginUtil;

    @Autowired
    BuffCacheService buffCacheService;
    @Autowired
    private BuffAccountInfoConfig buffAccountInfoConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        for (String acountData : buffAccountInfoConfig.getAccount_information()) {
            BuffUserData buffUserData = new BuffUserData();
            String acount = acountData.split("-")[0];
            String pwd = acountData.split("-")[1];
            buffUserData.setAcount(acount);
            buffUserData.setPwd(pwd);
            int count = 0;
            String cookie = "";
            while (StrUtil.isEmpty(cookie) && count < 3) {
                cookie = buffCacheService.getCookie(acount, buffUserData);
                //获取steamid
                String steamId = buffCacheService.getSteamId(acount, cookie);
                buffUserData.setSteamId(steamId);
                break;
            }
            buffUserDataList.add(buffUserData);
        }
    }
}
