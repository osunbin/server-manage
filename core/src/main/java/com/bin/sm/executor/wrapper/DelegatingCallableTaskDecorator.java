package com.bin.sm.executor.wrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class DelegatingCallableTaskDecorator<V>
        implements Callable<Future<V>> {

    private final ExecutorService executor;
    private final Callable<V> callable;

    /**
     * @param callable Task to be executed
     * @param executor ExecutorService the task to be delegated to
     */
    DelegatingCallableTaskDecorator(Callable<V> callable, ExecutorService executor) {
        this.executor = executor;
        this.callable = callable;
    }

    @Override
    public Future<V> call() {
        return executor.submit(callable);
    }

}
