package com.bin.sm.shutdown;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  入口计数
 *  http、定时任务、mq...
 */
public class GracefulShutdownManager {

    private static final AtomicBoolean shutdown = new AtomicBoolean(false);

    private AtomicInteger rootCount = new AtomicInteger(0);

    private Map<String, AtomicInteger> shutdownMap = new ConcurrentHashMap<>();

    public static boolean isShutdown() {
        return shutdown.get();
    }

    public static void shutdown() {
        shutdown.set(true);
    }

    public int inc(String key) {
        AtomicInteger atomicInteger = shutdownMap.get(key);
        if (atomicInteger == null) {
            atomicInteger = shutdownMap.computeIfAbsent(key,
                    k -> new AtomicInteger(0));
        }
        return atomicInteger.incrementAndGet();
    }


    public int dec(String key) {
        AtomicInteger atomicInteger = shutdownMap.get(key);
        if (atomicInteger == null) {
            return -1;
        }
        return atomicInteger.decrementAndGet();
    }

    public int inc() {
        return rootCount.incrementAndGet();
    }

    public int dec() {
        return rootCount.decrementAndGet();
    }

    // 没有请求了
    public boolean isGraceful() {
        int sum = shutdownMap.values().stream().mapToInt(AtomicInteger::getPlain).sum();
        return sum + rootCount.getPlain() == 0;
    }
}
