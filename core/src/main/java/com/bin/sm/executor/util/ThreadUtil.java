package com.bin.sm.executor.util;

import static com.bin.sm.util.Preconditions.checkNotNull;

public class ThreadUtil {

    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<Long>();

    private ThreadUtil() {
    }


    public static long getThreadId() {
        final Long threadId = THREAD_LOCAL.get();
        if (threadId != null) {
            return threadId;
        }
        return Thread.currentThread().getId();
    }


    public static void setThreadId(long threadId) {
        THREAD_LOCAL.set(threadId);
    }


    public static void removeThreadId() {
        THREAD_LOCAL.remove();
    }



    public static String createThreadName(String hzName, String name) {
        checkNotNull(name, "name can't be null");
        return "sm." + hzName + "." + name;
    }


    public static String createThreadPoolName(String hzName, String poolName) {
        return createThreadName(hzName, poolName) + ".thread-";
    }



}
