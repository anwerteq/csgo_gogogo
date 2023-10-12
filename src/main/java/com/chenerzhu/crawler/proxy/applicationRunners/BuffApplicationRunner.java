package com.chenerzhu.crawler.proxy.applicationRunners;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.cache.BuffCacheService;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.bufflogin.BuffAccountInfoConfig;
import com.chenerzhu.crawler.proxy.util.bufflogin.BuffAutoLoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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


    @Autowired
    BuffAutoLoginUtil buffAutoLoginUtil;

    @Autowired
    BuffCacheService buffCacheService;
    @Autowired
    private BuffAccountInfoConfig buffAccountInfoConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (buffAccountInfoConfig.getAccount_information().isEmpty()) {
            log.error("未加载到buff账号，请检查[buff.account_information]配置,退出脚本中");
            SleepUtil.sleep(5000);
            System.exit(1);
        }
        for (String acountData : buffAccountInfoConfig.getAccount_information()) {
            BuffUserData buffUserData = new BuffUserData();
            String acount = acountData.split("-")[0];
            String pwd = acountData.split("-")[1];
            buffUserData.setAcount(acount);
            buffUserData.setPwd(pwd);
            int count = 0;
            String cookie = "";
            while (StrUtil.isEmpty(cookie) && count++ < 3) {
                cookie = buffCacheService.getCookie(acount, buffUserData);
                //获取steamid
                String steamId = buffCacheService.getSteamId(acount, cookie);
                buffUserData.setSteamId(steamId);
                break;
            }
            buffUserDataList.add(buffUserData);
        }
        for (BuffUserData buffUserData : buffUserDataList) {
            log.info("加载buff账号：{}成功",buffUserData.getAcount());
        }
    }
}
