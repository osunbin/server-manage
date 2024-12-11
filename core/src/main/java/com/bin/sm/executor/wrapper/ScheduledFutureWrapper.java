package com.bin.sm.executor.wrapper;


import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ScheduledFutureWrapper<V> implements ScheduledFuture<V> {

    private  ScheduledFuture<Future<V>> scheduledFuture;
    public ScheduledFutureWrapper(ScheduledFuture<Future<V>> schedule) {
       this.scheduledFuture = schedule;
    }


    @Override
    public long getDelay(TimeUnit unit) {
        return scheduledFuture.getDelay(unit);
    }

    @Override
    public int compareTo(Delayed o) {
        return scheduledFuture.compareTo(o);
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return scheduledFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return scheduledFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return scheduledFuture.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return scheduledFuture.get().get();
    }

    @Override
    public V get(long timeout,TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return scheduledFuture.get(timeout,unit).get(timeout,unit);
    }
}
