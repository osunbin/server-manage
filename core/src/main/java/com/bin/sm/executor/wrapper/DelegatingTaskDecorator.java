package com.bin.sm.executor.wrapper;

import java.util.concurrent.Executor;

class DelegatingTaskDecorator implements Runnable {

    private final Executor executor;
    private final Runnable runnable;

    /**
     * @param runnable Task to be executed
     * @param executor Executor the task to be delegated to
     */
    DelegatingTaskDecorator(Runnable runnable, Executor executor) {
        this.executor = executor;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        executor.execute(runnable);
    }

    @Override
    public String toString() {
        return "DelegateDecorator{"
                + "executor=" + executor
                + ", runnable=" + runnable
                + '}';
    }
}