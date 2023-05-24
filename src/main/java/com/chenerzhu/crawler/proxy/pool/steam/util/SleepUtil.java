package com.chenerzhu.crawler.proxy.pool.steam.util;

/**
 * 睡眠工具类
 */
public class SleepUtil {

    public static void sleep(){
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
