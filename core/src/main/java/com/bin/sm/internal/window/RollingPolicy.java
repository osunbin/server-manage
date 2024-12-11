package com.bin.sm.internal.window;


import com.bin.sm.internal.function.Function2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RollingPolicy {
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private Lock readLock = rwLock.readLock();
    private Lock writeLock = rwLock.writeLock();

    private int size;
    private Window window;
    private int offset;
    private long bucketDuration;
    private long lastAppendTime;

    public RollingPolicy(Window window, long bucketDuration) {
        this.window = window;
        this.size = window.size();
        this.offset = 0;
        this.bucketDuration = bucketDuration;
        lastAppendTime = System.currentTimeMillis();
    }

    public int timespan() {
        int v = (int) ((System.currentTimeMillis() - lastAppendTime) / bucketDuration);
        if (v > -1)
            return v;
        return size;
    }


    public void apply(Function2<Integer, Double> func, double val) {

        writeLock.lock();
        try {
            int timespan = timespan();
            int oriTimespan = timespan;
            int oriOffset = offset();
            if (timespan > 0) {
                int oriSize = size();
                int start = (oriOffset + timespan) % oriSize;
                int end = (oriOffset + timespan) % oriSize;
                if (timespan > oriSize)
                    timespan = oriSize;
                window.resetBuckets(start, timespan);
                setOffset(end);
                setLastAppendTime(lastAppendTime() + (oriTimespan * bucketDuration));
            }
            func.apply(oriOffset, val);
        } finally {
            writeLock.unlock();
        }
    }


    public void append(double val) {
        apply((offset, v) -> window.append(offset, v), val);
    }

    public void add(double val) {
        apply((offset, v) -> window.add(offset, v), val);
    }


    public double reduce(ReduceBucket func) {
        readLock.lock();
        double reduce = 0;
        try {
            int timespan = timespan();
            int count = size() - timespan;
            if (count > 0) {
                int offset = offset() + timespan ; // + 1 TODO
                if (offset > size())
                    offset = offset - size;
                reduce = func.reduce(window.iterator(offset, count));
            }
            return reduce;
        } finally {
            readLock.unlock();
        }
    }


    public int offset() {
        return offset;
    }

    public int size() {
        return size;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public long lastAppendTime() {
        return lastAppendTime;
    }

    public void setLastAppendTime(long lastAppendTime) {
        this.lastAppendTime = lastAppendTime;
    }

    public Lock readLock() {
        return readLock;
    }
}
