package com.bin.sm.internal.window;

import java.util.concurrent.locks.Lock;

public class RollingCounter {
    private RollingPolicy policy;

    public RollingCounter(int size, long bucketDuration) {
        policy = new RollingPolicy(new Window(size), bucketDuration);
    }

    public void add(long val) {
        if (val < 0)
            throw new IllegalArgumentException("stat/metric: cannot decrease in value. val: " + val);
        policy.add((double) val);
    }


    public double reduce(ReduceBucket func) {
        return policy.reduce(func);
    }

    public double avg() {
        return policy.reduce(ReduceBucket::avg);
    }

    public double min() {
        return policy.reduce(ReduceBucket::min);
    }

    public double max() {
        return policy.reduce(ReduceBucket::max);
    }

    public double sum() {
        return policy.reduce(ReduceBucket::sum);
    }


    public double value() {
        return sum();
    }


    public int timespan() {
        Lock lock = policy.readLock();
        lock.lock();
        try {
            return policy.timespan();
        } finally {
            lock.unlock();
        }
    }

}
