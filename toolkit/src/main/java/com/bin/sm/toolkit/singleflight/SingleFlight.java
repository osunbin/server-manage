package com.bin.sm.toolkit.singleflight;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *  防止缓存击穿
 */
public class SingleFlight<V> {
    private ConcurrentHashMap<String, SegmentLock<V>> map = new ConcurrentHashMap<>();


    public SingleFlight() {

    }

    public SingleFlightResult<V> getResult(String key, Callable<V> callable, long timeout, TimeUnit timeUnit)
            throws InterruptedException, TimeoutException {
        if (key == null) throw new NullPointerException("key is null");
        SegmentLock<V> oldSegmentLock = map.get(key); // 查：如果能查的到那么就是能用的，有可能是已经生产好的，也有可能正在生产
        if (oldSegmentLock != null) {
            return getResult(oldSegmentLock, timeout, timeUnit);
        }

        SegmentLock<V> segmentLock = new SegmentLock<>(key);
        oldSegmentLock = map.putIfAbsent(key, segmentLock); // 增：防止了两个生产者同时生产，ConcurrentHashMap的putIfAbsent是线程安全的
        if (oldSegmentLock == null) { // 这里是怕两个生产者，抢着并发生产 double check key值
            V v;
            try {
                v = callable.call(); // 开始阻塞同步生产
                segmentLock.complete(v);
                map.remove(key, segmentLock);
            } catch (Throwable e) {
                segmentLock.exception(e);
                map.remove(key, segmentLock);
                throw new RuntimeException(e);
            }
            return SingleFlightResult.ofExec(v);
        } else {
            return getResult(oldSegmentLock, timeout, timeUnit);
        }
    }

    private SingleFlightResult<V> getResult(SegmentLock<V> oldSegmentLock,
                                            long timeout, TimeUnit timeUnit)
            throws InterruptedException, TimeoutException {
        if (oldSegmentLock.isError()) {
            return SingleFlightResult.ofError(oldSegmentLock.getThrowable());
        }
        return SingleFlightResult.ofWait(oldSegmentLock.get(timeout, timeUnit));
    }
}
