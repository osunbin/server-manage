package com.bin.sm.ratelimiter.concurrency.limit.measurement;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ExpAvgMeasurement implements Measurement {
    private Double value = 0.0;
    private Double sum = 0.0;
    private final int window;
    private final int warmupWindow;
    private int count = 0;
    // 600    10
    public ExpAvgMeasurement(int window, int warmupWindow) {
        this.window = window;
        this.warmupWindow = warmupWindow;
    }

    @Override
    public Number add(Number sample) {
        if (count < warmupWindow) {
            count++;
            sum += sample.doubleValue();
            value = sum / count;
        } else {
            double factor = factor(window);
            value = value * (1-factor) + sample.doubleValue() * factor;
        }
        return value;
    }

    private static double factor(int n) {
        return 2.0 / (n + 1);
    }

    @Override
    public Number get() {
        return value;
    }

    @Override
    public void reset() {
        value = 0.0;
        count = 0;
        sum = 0.0;
    }

    @Override
    public void update(Function<Number, Number> operation) {
        this.value = operation.apply(value).doubleValue();
    }
}
