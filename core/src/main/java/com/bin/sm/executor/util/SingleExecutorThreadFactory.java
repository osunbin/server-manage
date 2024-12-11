package com.bin.sm.executor.util;

import com.bin.sm.executor.ManagedThread;

import java.util.concurrent.ThreadFactory;

public final class SingleExecutorThreadFactory implements ThreadFactory {

    private final String threadName;

    public static SingleExecutorThreadFactory of(String threadName) {
        return new SingleExecutorThreadFactory(threadName);
    }

    public SingleExecutorThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public  Thread newThread(Runnable r) {
        final Thread t = new ManagedThreadName(r);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        return t;
    }

    private class ManagedThreadName extends ManagedThread {

        ManagedThreadName(Runnable target) {
            super(target, threadName);
        }
    }
}