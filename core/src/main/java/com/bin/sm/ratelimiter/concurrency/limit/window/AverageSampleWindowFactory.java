package com.bin.sm.ratelimiter.concurrency.limit.window;

public class AverageSampleWindowFactory implements SampleWindowFactory {
    private static final AverageSampleWindowFactory INSTANCE = new AverageSampleWindowFactory();

    private AverageSampleWindowFactory() {}

    public static AverageSampleWindowFactory create() {
        return INSTANCE;
    }

    @Override
    public ImmutableAverageSampleWindow newInstance() {
        return new ImmutableAverageSampleWindow();
    }
}