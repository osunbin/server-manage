package com.bin.sm.executor.util;

import com.bin.sm.executor.ManagedThread;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PoolExecutorThreadFactory implements ThreadFactory {


    private final String threadNamePrefix;
    private final AtomicInteger idGen = new AtomicInteger(0);
    // to reuse previous thread IDs
    private final Queue<Integer> idQ = new LinkedBlockingQueue<Integer>(1000);
    private final boolean daemon;

    public PoolExecutorThreadFactory(String threadNamePrefix) {
        this(threadNamePrefix, false);
    }

    public PoolExecutorThreadFactory(String threadNamePrefix, boolean daemon) {
        this.daemon = daemon;
        this.threadNamePrefix = threadNamePrefix;
    }


    @Override
    public  Thread newThread(Runnable r) {
        final Thread t = createThread(r);
        t.setDaemon(daemon);

        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }


    private Thread createThread(Runnable r) {
        Integer id = idQ.poll();
        if (id == null) {
            id = idGen.incrementAndGet();
        }
        String name = threadNamePrefix + id;
        return new ManagedThreadId(r, name, id);
    }


    class ManagedThreadId extends ManagedThread {

        private final int id;

        public ManagedThreadId(Runnable target, String name, int id) {
            super(target, name);
            this.id = id;
        }

        @Override
        protected void afterRun() {
            try {
                idQ.offer(id);
            } catch (Throwable ignored) {
            }
        }
    }
}
