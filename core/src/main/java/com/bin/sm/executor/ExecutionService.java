package com.bin.sm.executor;


import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public interface ExecutionService {


    ManagedExecutorService register(String key, int poolSize, int queueCapacity);

    ManagedExecutorService register(String key, int poolSize, int queueCapacity, ThreadFactory threadFactory);


    ManagedExecutorService getExecutor(String name);

    void shutdownExecutor(String name);

    void execute(String name, Runnable command);

    Future<?> submit(String name, Runnable task);

    <T> Future<T> submit(String name, Callable<T> task);

    ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit);

    ScheduledFuture<?> schedule(String name, Runnable command, long delay, TimeUnit unit);


    ScheduledExecutorService getScheduler(String name);




}
