package com.bin.sm.executor.middleware;

import com.bin.sm.internal.collection.CopyOnWriteMap;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class RocketMQThreadPool {

    private static  Map<String, ThreadPoolExecutor> threadPools = new CopyOnWriteMap<>();


    public static void registerThreadPool(String threadPoolName, ThreadPoolExecutor threadPool) {
        if (threadPool != null)
            threadPools.put(threadPoolName, threadPool);
    }
}
