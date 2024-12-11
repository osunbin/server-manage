package com.bin.sm.executor;


import com.bin.sm.executor.support.TaskDecorator;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程id-message-produce
 * 线程名称- dynamic-threedpool-example
 * 核心线程
 * 最大线程
 * 当前活线程数
 * 最大任务数
 * 任务完成数
 * 线程池任务总量
 * 队列类型： blockQueue
 * allowCoreThread-核心线程允许回收
 * keepAliveTime-线程存活时间
 * taskWaitTime-任务等待超时
 * taskRunTime-执行超时
 * 队列容量
 * 队列个数
 * 拒绝策略：
 * 拒绝数量
 */
public class ExtensibleThreadPoolExecutor extends ThreadPoolExecutor implements ManagedExecutorService {


    private String threadPoolId;
    private long awaitTerminationMillis;
    private final ThreadLocal<Long> startTimes = new ThreadLocal<>();
    private final AtomicInteger submittedCount = new AtomicInteger(0);
    private long executeTimeOut;
    private TaskDecorator taskDecorator;

    public ExtensibleThreadPoolExecutor(String threadPoolId, int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                        TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.threadPoolId = threadPoolId;
    }

    public ExtensibleThreadPoolExecutor(String threadPoolId, int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                        TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,handler);
        this.threadPoolId = threadPoolId;

    }



    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        startTimes.set(System.currentTimeMillis());
    }

    /**
     * 计算当前有多少任务正在处理  优雅停机
     */
    @Override
    public void execute(Runnable command) {
        submittedCount.incrementAndGet();
        command = taskDecorator.decorate(command);
        super.execute(command);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        submittedCount.decrementAndGet();
        try {
            Long start = startTimes.get();
            if (t != null && start != null) {
                long taskExecuteTime =
                        System.currentTimeMillis() - start;
                if (taskExecuteTime <= executeTimeOut) {
                    return;
                }

            }

        } finally {
            startTimes.remove();
        }
    }


    public void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

    public void setExecuteTimeOut(long executeTimeOut) {
        this.executeTimeOut = executeTimeOut;
    }

    public void setAwaitTerminationMillis(long awaitTerminationMillis) {
        this.awaitTerminationMillis = awaitTerminationMillis;
    }

    @Override
    public String getKey() {
        return threadPoolId;
    }

    @Override
    public int getQueueSize() {
        return 0;
    }

    @Override
    public int getRemainingQueueCapacity() {
        return 0;
    }

    @Override
    public void shutdown() {
        if (this.awaitTerminationMillis <= 0) {
            super.shutdown();
            return;
        }
        super.shutdown();

        try {
            boolean isTerminated = awaitTermination(awaitTerminationMillis, TimeUnit.MILLISECONDS);
            if (!isTerminated) {
                //  log.warn("Timed out while waiting for executor {} to terminate.", threadPoolId);

            }
        } catch (InterruptedException e) {
            //  log.warn("Interrupted while waiting for executor {} to terminate.", threadPoolId);
        }

    }

    @Override
    public List<Runnable> shutdownNow() {
        if (this.awaitTerminationMillis <= 0) {
            return super.shutdownNow();
        }
        List<Runnable> runnables = super.shutdownNow();

        try {
            boolean isTerminated = awaitTermination(awaitTerminationMillis, TimeUnit.MILLISECONDS);
            if (!isTerminated) {
                //  log.warn("Timed out while waiting for executor {} to terminate.", threadPoolId);

            }
        } catch (InterruptedException e) {
            //  log.warn("Interrupted while waiting for executor {} to terminate.", threadPoolId);
        }
        return runnables;
    }


    private static class RejectedExecutionHandlerWrapper implements RejectedExecutionHandler {

        private RejectedExecutionHandler rejectedExecutionHandler;

        public RejectedExecutionHandlerWrapper(RejectedExecutionHandler rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            rejectedExecutionHandler.rejectedExecution(r,executor);
        }
    }

//    public ThreadPoolRunStateInfo getPoolRunState(String threadPoolId, Executor executor) {
//        ThreadPoolExecutor actualExecutor = (ThreadPoolExecutor) executor;
//        int activeCount = actualExecutor.getActiveCount();
//        int largestPoolSize = actualExecutor.getLargestPoolSize();
//        BlockingQueue<Runnable> blockingQueue = actualExecutor.getQueue();
//        long rejectCount = NO_REJECT_COUNT_NUM;
//        if (Objects.equals(actualExecutor.getClass().getName(), "cn.hippo4j.core.executor.DynamicThreadPoolExecutor")) {
//            Object actualRejectCountNum = ReflectUtil.invoke(actualExecutor, "getRejectCountNum");
//            if (actualRejectCountNum != null) {
//                rejectCount = (long) actualRejectCountNum;
//            }
//        }
//        ThreadPoolRunStateInfo stateInfo = ThreadPoolRunStateInfo.builder()
//                .tpId(threadPoolId)
//                .activeSize(activeCount)
//                .poolSize(actualExecutor.getPoolSize())
//                .completedTaskCount(actualExecutor.getCompletedTaskCount())
//                .largestPoolSize(largestPoolSize)
//                .currentLoad(CalculateUtil.divide(activeCount, actualExecutor.getMaximumPoolSize()) + "")
//                .clientLastRefreshTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
//                .peakLoad(CalculateUtil.divide(largestPoolSize, actualExecutor.getMaximumPoolSize()) + "")
//                .queueSize(blockingQueue.size())
//                .queueRemainingCapacity(blockingQueue.remainingCapacity())
//                .rejectCount(rejectCount)
//                .timestamp(System.currentTimeMillis())
//                .build();
//        stateInfo.setCoreSize(actualExecutor.getCorePoolSize());
//        stateInfo.setMaximumSize(actualExecutor.getMaximumPoolSize());
//        stateInfo.setQueueType(blockingQueue.getClass().getSimpleName());
//        stateInfo.setQueueCapacity(blockingQueue.size() + blockingQueue.remainingCapacity());
//        return supplement(stateInfo);
//    }
}
