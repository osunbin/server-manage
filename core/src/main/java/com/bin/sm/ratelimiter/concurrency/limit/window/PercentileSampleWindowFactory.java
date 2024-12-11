package com.bin.sm.ratelimiter.concurrency.limit.window;

import com.google.common.base.Preconditions;

public class PercentileSampleWindowFactory implements SampleWindowFactory {
    private final double percentile;
    private final int windowSize;

    private PercentileSampleWindowFactory(double percentile, int windowSize) {
        this.percentile = percentile;
        this.windowSize = windowSize;
    }

    public static PercentileSampleWindowFactory of(double percentile, int windowSize) {
        Preconditions.checkArgument(percentile > 0 && percentile < 1.0, "Percentile should belong to (0, 1.0)");
        return new PercentileSampleWindowFactory(percentile, windowSize);
    }

    @Override
    public ImmutablePercentileSampleWindow newInstance() {
        return new ImmutablePercentileSampleWindow(percentile, windowSize);
    }
}
