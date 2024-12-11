package com.bin.sm.executor;

import java.util.concurrent.ExecutorService;

public interface ManagedExecutorService extends ExecutorService {

    String getKey();


    long getCompletedTaskCount();


    int getMaximumPoolSize();


    int getPoolSize();


    int getQueueSize();


    int getRemainingQueueCapacity();



}
