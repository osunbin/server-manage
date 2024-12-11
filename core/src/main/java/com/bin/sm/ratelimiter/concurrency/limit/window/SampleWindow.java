package com.bin.sm.ratelimiter.concurrency.limit.window;

/**
 * Implementations of this interface are being used to track immutable samples in an AtomicReference
 *
 * @see com.netflix.concurrency.limits.limit.WindowedLimit
 */
public interface SampleWindow {
    SampleWindow addSample(long rtt, int inflight, boolean dropped);

    long getCandidateRttNanos();

    long getTrackedRttNanos();

    int getMaxInFlight();

    int getSampleCount();

    boolean didDrop();
}
