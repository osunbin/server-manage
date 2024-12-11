package com.bin.sm.executor.wrapper;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.bin.sm.util.Preconditions.checkNotNull;

public class DelegatingTaskScheduler implements ScheduledExecutorService {

    private final ScheduledThreadPoolExecutor scheduledExecutorService;
    private final ExecutorService executor;

    public DelegatingTaskScheduler(ScheduledThreadPoolExecutor scheduledExecutorService, ExecutorService executor) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.executor = executor;
    }

    
    @Override
    public ScheduledFuture<?> schedule( Runnable command, long delay,  TimeUnit unit) {
        checkNotNull(command);
        Runnable decoratedTask = new DelegatingTaskDecorator(command, executor);
        return scheduledExecutorService.schedule(decoratedTask, delay, unit);
    }

    
    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> command, long delay, TimeUnit unit) {
        checkNotNull(command);
        Callable<Future<V>> decoratedTask = new DelegatingCallableTaskDecorator<V>(command, executor);
        ScheduledFuture<Future<V>> schedule = scheduledExecutorService.schedule(decoratedTask, delay, unit);
        return new ScheduledFutureWrapper<>(schedule);
    }

    
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period,  TimeUnit unit) {
        checkNotNull(command);
        Runnable decoratedTask = new DelegatingTaskDecorator(command, executor);
        return scheduledExecutorService.scheduleAtFixedRate(decoratedTask, initialDelay, period,unit);
    }

    
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,  TimeUnit unit) {
        checkNotNull(command);
        Runnable decoratedTask = new DelegatingTaskDecorator(command, executor);
        return scheduledExecutorService.scheduleWithFixedDelay(decoratedTask, initialDelay, delay,unit);
    }

    @Override
    public void shutdown() {

    }

    
    @Override
    public List<Runnable> shutdownNow() {
        return List.of();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout,  TimeUnit unit) throws InterruptedException {
        return false;
    }

    
    @Override
    public <T> Future<T> submit( Callable<T> task) {
        return null;
    }

    
    @Override
    public <T> Future<T> submit( Runnable task, T result) {
        return null;
    }

    
    @Override
    public Future<?> submit( Runnable task) {
        return null;
    }

    
    @Override
    public <T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return List.of();
    }

    
    @Override
    public <T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> tasks, long timeout,  TimeUnit unit) throws InterruptedException {
        return List.of();
    }

    
    @Override
    public <T> T invokeAny( Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public <T> T invokeAny( Collection<? extends Callable<T>> tasks, long timeout,  TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    @Override
    public void execute( Runnable command) {
        executor.execute(command);
    }
}
