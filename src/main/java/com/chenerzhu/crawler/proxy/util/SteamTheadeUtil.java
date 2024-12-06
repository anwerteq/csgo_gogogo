package com.chenerzhu.crawler.proxy.util;

import com.chenerzhu.crawler.proxy.util.steamlogin.SteamUserDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SteamTheadeUtil {

    public static List<SteamUserDate> steamUserDates = new ArrayList<>();

    public static ThreadLocal<SteamUserDate> steamUserDateTL = new ThreadLocal<>();
    

    public static SteamUserDate  setThreadSteamUserDate(String name){
        Optional<SteamUserDate> first = steamUserDates.stream().filter(o -> name.toLowerCase().equals(o.getAccount_name().toLowerCase())).findFirst();
        if (!first.isPresent()) {
            throw new RuntimeException("账号："+name+"不存");
        }
        SteamUserDate steamUserDate1 = (SteamUserDate) first.get();
        SteamTheadeUtil.steamUserDateTL.set(steamUserDate1);
        return steamUserDate1;
    }

    public static SteamUserDate getThreadSteamUserDate(){
        SteamUserDate steamUserDate = steamUserDateTL.get();
        if (steamUserDate == null){
            throw new RuntimeException("该线程未设置 steam信息");

        }
        return steamUserDate;
    }
}
