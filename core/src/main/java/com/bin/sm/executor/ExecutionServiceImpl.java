package com.bin.sm.executor;

import com.bin.sm.executor.util.PoolExecutorThreadFactory;
import com.bin.sm.executor.util.SingleExecutorThreadFactory;
import com.bin.sm.executor.wrapper.DelegatingTaskScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.bin.sm.executor.util.ThreadUtil.createThreadPoolName;
import static java.lang.Thread.currentThread;

public class ExecutionServiceImpl implements ExecutionService{

    private static final int CORE_POOL_SIZE = 3;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final long INITIAL_DELAY = 1000;
    private static final long PERIOD = 100;
    private static final long AWAIT_TIME = 3;

    private final Logger logger = LoggerFactory.getLogger(ExecutionService.class);

    private final ScheduledThreadPoolExecutor globalTaskScheduler;

    private final ConcurrentMap<String, ManagedExecutorService> executors = new ConcurrentHashMap<>();
    private final String appName;


    public ExecutionServiceImpl(String appName) {
        this.appName = appName;
        this.globalTaskScheduler = new ScheduledThreadPoolExecutor(1, SingleExecutorThreadFactory.of(createThreadPoolName(appName, "scheduled")));
    }


    @Override
    public ManagedExecutorService register(String key, int defaultPoolSize, int defaultQueueCapacity) {
        return register(key, defaultPoolSize, defaultQueueCapacity,  null);

    }

    @Override
    public ManagedExecutorService register(String key, int defaultPoolSize, int defaultQueueCapacity, ThreadFactory threadFactory) {

        logger.info("register threadPoolKey:{} PoolSize:{} QueueCapacity:{}",key,defaultPoolSize,defaultQueueCapacity);
        ManagedExecutorService executor = createExecutor(key, defaultPoolSize, defaultQueueCapacity,threadFactory);
        if (executors.putIfAbsent(key, executor) != null) {
            throw new IllegalArgumentException("ExecutorService['" + key + "'] already exists!");
        }
        return executor;
    }

    private ManagedExecutorService createExecutor(String name, int poolSize, int queueCapacity,
                                                  ThreadFactory threadFactory) {
        if (threadFactory == null) {
            String threadNamePrefix = createThreadPoolName(appName, name);
            threadFactory = new PoolExecutorThreadFactory(threadNamePrefix);
        }
        // name-通用线程 key-all  name=订单线程 key-orderCreate
        ExtensibleThreadPoolExecutor pool = new ExtensibleThreadPoolExecutor(name, poolSize, poolSize,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueCapacity), threadFactory);
        pool.allowCoreThreadTimeOut(true);
        return pool;
    }


    @Override
    public ManagedExecutorService getExecutor(String name) {
        return executors.get(name);
    }

    @Override
    public ScheduledExecutorService getScheduler(String name) {
        return new DelegatingTaskScheduler(globalTaskScheduler, getExecutor(name));
    }



    @Override
    public void execute(String name, Runnable command) {
        getExecutor(name).execute(command);
    }


    @Override
    public Future<?> submit(String name, Runnable task) {
        return getExecutor(name).submit(task);
    }

    @Override
    public <T> Future<T> submit(String name, Callable<T> task) {
        return getExecutor(name).submit(task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return globalTaskScheduler.schedule(command, delay, unit);
    }

    @Override
    public ScheduledFuture<?> schedule(String name, Runnable command, long delay, TimeUnit unit) {
        return getScheduler(name).schedule(command, delay, unit);
    }


    @Override
    public void shutdownExecutor(String name) {
        ExecutorService executorService = executors.remove(name);
        if (executorService != null) {
            executorService.shutdown();
        }
    }




    public void shutdown() {

        for (ExecutorService executorService : executors.values()) {
            executorService.shutdown();
        }

        globalTaskScheduler.shutdown();
        try {
            globalTaskScheduler.awaitTermination(AWAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            currentThread().interrupt();
        }
        executors.clear();
    }

}
