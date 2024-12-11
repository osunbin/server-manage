package com.bin.sm.toolkit.singleflight;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SegmentLock<V>{

    private   String key;
    private volatile V v;
    private Throwable throwable;
    CountDownLatch downLatch = new CountDownLatch(1);
    public SegmentLock(String key) {
        this.key = key;
    }

    public void complete(V v) {
        this.v = v;
        downLatch.countDown();
    }

    public void exception(Throwable t) {
        throwable = t;
        downLatch.countDown();
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        downLatch.await(timeout,unit);
        if (v != null) return v;
        throw new TimeoutException("key is timeout");
    }

    public boolean isError() {
        if (throwable != null) return true;
        return false;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}