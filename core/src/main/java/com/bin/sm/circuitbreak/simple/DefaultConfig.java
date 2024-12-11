package com.bin.sm.circuitbreak.simple;

import com.bin.sm.circuitbreak.CircuitBreakerConfig;

public class DefaultConfig extends CircuitBreakerConfig {
    // 熔断时间
    protected int sleepWindowInMilliseconds;
    // 异常比例
    protected int errorThresholdPercentage = 20;
    // 熔断后，再次通过的做大请求
    protected int maxHalfOpenPass = 20;
}
