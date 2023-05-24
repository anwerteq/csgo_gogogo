package com.chenerzhu.crawler.proxy.steam.util;

/**
 * 睡眠工具类
 */
public class SleepUtil {

    public static void sleep(){
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
