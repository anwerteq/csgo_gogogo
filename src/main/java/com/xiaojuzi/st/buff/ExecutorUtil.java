package com.xiaojuzi.st.buff;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程池定义
 */
public class ExecutorUtil {

  public static ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);
}
