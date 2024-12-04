package com.xiaojuzi.st.config;


import com.xiaojuzi.st.steam.entity.Cookeis;
import com.xiaojuzi.st.steam.repository.CookeisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CookiesConfig  implements ApplicationRunner {


    /**
     * buff cookies
     */
    public static ThreadLocal<String> buffCookies = new ThreadLocal<>();

    /**
     * steam cookies
     */
    public static ThreadLocal<String> steamCookies = new ThreadLocal<>();
    @Autowired
    CookeisRepository cookeisRepository;

    /**
     * 全部账号的cookies信息
     */
    public static List<Cookeis> cookeisList = new ArrayList<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        refreshCookie();
    }

    /**
     * 刷新cookie
     */
    public  void refreshCookie(){
//        List<Cookeis> all = cookeisRepository.findAll();
//        List<Cookeis> notStop = all.stream().filter(cookeis -> "1".equals(cookeis.getIs_top())).collect(Collectors.toList());
//        cookeisList = notStop;
//        log.info("账号的cookie信息，加载完成，一共{}个账号",cookeisList.size());
    }
}
