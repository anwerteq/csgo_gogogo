package com.chenerzhu.crawler.proxy.util.steamlogin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mr.W on 2017/6/17.
 * 时间相关util
 */
public class TimeUtil {

    private static final Logger log = LoggerFactory.getLogger(TimeUtil.class);

    public static Long diff = 0L;

    public static Long getTimeStamp() {
        Long currentTime = System.currentTimeMillis() / 1000;
        return currentTime + diff;
    }

    public static Long getUnixTime() {
        return System.currentTimeMillis() / 1000;
    }

}
