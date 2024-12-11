package com.bin.sm.executor.support;


import com.bin.sm.executor.ExtensibleThreadPoolExecutor;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("all")
public class ThreadPoolBuilder {

    //得到线程池的核心线程数量，这个数量是根据CPU核心数量计算出来的
    private int corePoolSize = calculateCoreNum();

    //线程池最大线程数量
    private int maxPoolSize = corePoolSize;

    //存活时间
    private long keepAliveTime = 30000L;

    //时间单位
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    //执行任务超时时间
    private long executeTimeOut = 10000L;

    //队列容量
    private int capacity = 512;

    //队列类型，默认使用ResizableCapacityLinkedBlockingQueue队列
    private BlockingQueueTypeEnum blockingQueueType = BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE;

    //线程池队列
    private BlockingQueue<Runnable> workQueue;

    //拒绝策略
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();

    //是否为守护线程
    private boolean isDaemon = false;

    //线程池现成的前缀名称
    private String threadNamePrefix;

    //创建线程的线程工厂
    private ThreadFactory threadFactory;

    //线程池Id
    private String threadPoolId;

    //线程池要执行的任务的装饰器，这个装饰器很有用，是一个挺好的扩展点
    //在这个装饰器的包装下，用户可以在线程池要执行的真正任务之前做一些额外的操作
    //在这个动态线程池框架中， 有一个TaskTraceBuilderHandler对象，这就是一个装饰器对象
    //在它的包装下，在每一个任务执行之前，都会把任务超时标志放到执行人物的线程中
    //如果线程执行的某个任务超时了，就可以使用这个标志告警通知了
    private TaskDecorator taskDecorator;

    //线程池关闭时，等待剩余任务执行的最大时间
    private Long awaitTerminationMillis = 5000L;

    //线程池关闭时是否等待正在执行的任务执行完毕
    private Boolean waitForTasksToCompleteOnShutdown = true;

    //是否允许超过存活时间的核心线程终止工作
    private Boolean allowCoreThreadTimeOut = false;


    //得到线程池核心线程数量的方法
    private Integer calculateCoreNum() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        return new BigDecimal(cpuCoreNum).divide(new BigDecimal("0.5")).intValue();
    }


    public ThreadPoolBuilder threadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
        return this;
    }

    /**
     * Thread factory.
     *
     * @param threadFactory thread factory
     * @return thread-pool builder
     */
    public ThreadPoolBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * Thread factory.
     *
     * @param threadNamePrefix thread name prefix
     * @param isDaemon         is daemon
     * @return thread-pool builder
     */
    public ThreadPoolBuilder threadFactory(String threadNamePrefix, Boolean isDaemon) {
        this.threadNamePrefix = threadNamePrefix;
        this.isDaemon = isDaemon;
        return this;
    }

    /**
     * Core pool size.
     *
     * @param corePoolSize core pool size
     * @return thread-pool builder
     */
    public ThreadPoolBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * Max pool num.
     *
     * @param maxPoolSize max pool num
     * @return thread-pool builder
     */
    public ThreadPoolBuilder maxPoolNum(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        if (maxPoolSize < this.corePoolSize) {
            this.corePoolSize = maxPoolSize;
        }
        return this;
    }

    /**
     * Single pool.
     *
     * @return thread-pool builder
     */
    public ThreadPoolBuilder singlePool() {
        int singleNum = 1;
        this.corePoolSize = singleNum;
        this.maxPoolSize = singleNum;
        return this;
    }

    /**
     * Single pool.
     *
     * @param threadNamePrefix thread name prefix
     * @return thread-pool builder
     */
    public ThreadPoolBuilder singlePool(String threadNamePrefix) {
        int singleNum = 1;
        this.corePoolSize = singleNum;
        this.maxPoolSize = singleNum;
        this.threadNamePrefix = threadNamePrefix;
        return this;
    }

    /**
     * Pool thread size.
     *
     * @param corePoolSize core pool size
     * @param maxPoolSize  max pool size
     * @return thread-pool builder
     */
    public ThreadPoolBuilder poolThreadSize(int corePoolSize, int maxPoolSize) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    /**
     * Keep alive time.
     *
     * @param keepAliveTime keep alive time
     * @return thread-pool builder
     */
    public ThreadPoolBuilder keepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    /**
     * Time unit.
     *
     * @param timeUnit time unit
     * @return thread-pool builder
     */
    public ThreadPoolBuilder timeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    /**
     * Execute time-out.
     *
     * @param executeTimeOut execute time-out
     * @return thread-pool builder
     */
    public ThreadPoolBuilder executeTimeOut(long executeTimeOut) {
        this.executeTimeOut = executeTimeOut;
        return this;
    }

    /**
     * Keep alive time.
     *
     * @param keepAliveTime keep alive time
     * @param timeUnit      time unit
     * @return thread-pool builder
     */
    public ThreadPoolBuilder keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        return this;
    }

    /**
     * Capacity.
     *
     * @param capacity capacity
     * @return thread-pool builder
     */
    public ThreadPoolBuilder capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    /**
     * Work queue.
     *
     * @param queueType queue type
     * @param capacity  capacity
     * @return thread-pool builder
     */
    public ThreadPoolBuilder workQueue(BlockingQueueTypeEnum queueType, int capacity) {
        this.blockingQueueType = queueType;
        this.capacity = capacity;
        return this;
    }

    /**
     * Rejected.
     *
     * @param rejectedExecutionHandler rejected execution handler
     * @return thread-pool builder
     */
    public ThreadPoolBuilder rejected(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }

    /**
     * Work queue.
     *
     * @param blockingQueueType blocking queue type
     * @return thread-pool builder
     */
    public ThreadPoolBuilder workQueue(BlockingQueueTypeEnum blockingQueueType) {
        this.blockingQueueType = blockingQueueType;
        return this;
    }

    /**
     * Work queue.
     *
     * @param workQueue work queue
     * @return thread-pool builder
     */
    public ThreadPoolBuilder workQueue(BlockingQueue workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    /**
     * Thread-pool id.
     *
     * @param threadPoolId thread-pool id
     * @return thread-pool builder
     */
    public ThreadPoolBuilder threadPoolId(String threadPoolId) {
        this.threadPoolId = threadPoolId;
        return this;
    }

    /**
     * Task decorator.
     *
     * @param taskDecorator task decorator
     * @return thread-pool builder
     */
    public ThreadPoolBuilder taskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
        return this;
    }

    /**
     * Await termination millis.
     *
     * @param awaitTerminationMillis await termination millis
     * @return thread-pool builder
     */
    public ThreadPoolBuilder awaitTerminationMillis(long awaitTerminationMillis) {
        this.awaitTerminationMillis = awaitTerminationMillis;
        return this;
    }

    /**
     * Wait for tasks to complete on shutdown.
     *
     * @param waitForTasksToCompleteOnShutdown wait for tasks to complete on shutdown
     * @return thread-pool builder
     */
    public ThreadPoolBuilder waitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
        return this;
    }

    /**
     * Dynamic support.
     *
     * @param waitForTasksToCompleteOnShutdown wait for tasks to complete on shutdown
     * @param awaitTerminationMillis           await termination millis
     * @return thread-pool builder
     */
    public ThreadPoolBuilder dynamicSupport(boolean waitForTasksToCompleteOnShutdown, long awaitTerminationMillis) {
        this.awaitTerminationMillis = awaitTerminationMillis;
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
        return this;
    }

    /**
     * Allow core thread time-out.
     *
     * @param allowCoreThreadTimeOut core thread time-out
     * @return thread-pool builder
     */
    public ThreadPoolBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }


    //得到线程池构建器的方法
    public static ThreadPoolBuilder builder() {
        return new ThreadPoolBuilder();
    }


    //通过线程池Id创建动态线程池的方法
    public static ThreadPoolExecutor buildDynamicPoolById(String threadPoolId) {
        return ThreadPoolBuilder.builder().threadFactory(threadPoolId).threadPoolId(threadPoolId).build();
    }




    private  ExtensibleThreadPoolExecutor build(ThreadPoolBuilder builder) {
        ThreadFactory threadFactory = null;

        final String namePrefix = builder.threadNamePrefix;

        if (builder.threadFactory == null) {
            threadFactory = new ThreadFactory(){
                private AtomicLong count = new AtomicLong();
                @Override
                public Thread newThread(@NotNull Runnable r) {
                    final Thread thread = Executors.defaultThreadFactory().newThread(r);
                    if (null != builder.threadNamePrefix) {
                        thread.setName(builder.threadNamePrefix + "_" + count.getAndIncrement());
                    }
                    if (builder.isDaemon) {
                        thread.setDaemon(isDaemon);
                    }
                    return thread;
                }
            };
        }else {
            threadFactory = builder.threadFactory;
        }
        String threadPoolId = Optional.ofNullable(builder.threadPoolId)
                .orElse(builder.threadNamePrefix);

        if (builder.workQueue == null) {
            //设置任务队列，使用的是LinkedBlockingQueue
            if (builder.blockingQueueType == null) {
                builder.blockingQueueType = BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE;
            }
            //创建LinkedBlockingQueue队列
            builder.workQueue = BlockingQueueTypeEnum.createBlockingQueue(builder.blockingQueueType.getType(), builder.capacity);
        }

        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(threadPoolId,builder.corePoolSize,builder.maxPoolSize,builder.keepAliveTime,
                builder.timeUnit,builder.workQueue,threadFactory,builder.rejectedExecutionHandler);
        executor.allowCoreThreadTimeOut(builder.allowCoreThreadTimeOut);
        executor.setExecuteTimeOut(builder.executeTimeOut);
        executor.setTaskDecorator(builder.taskDecorator);
        executor.setAwaitTerminationMillis(builder.awaitTerminationMillis);
        return executor;
    }



    public ThreadPoolExecutor build() {
        return build(this);
    }


    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public long getExecuteTimeOut() {
        return executeTimeOut;
    }

    public int getCapacity() {
        return capacity;
    }

    public BlockingQueueTypeEnum getBlockingQueueType() {
        return blockingQueueType;
    }

    public BlockingQueue<Runnable> getWorkQueue() {
        return workQueue;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    public boolean isDaemon() {
        return isDaemon;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public String getThreadPoolId() {
        return threadPoolId;
    }

    public TaskDecorator getTaskDecorator() {
        return taskDecorator;
    }

    public Long getAwaitTerminationMillis() {
        return awaitTerminationMillis;
    }

    public Boolean getWaitForTasksToCompleteOnShutdown() {
        return waitForTasksToCompleteOnShutdown;
    }

    public Boolean getAllowCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }
}
