package com.bin.sm.circuitbreak;

public class CircuitBreakerConfig {

    protected int bucket = 10;
    protected  long window = 10 * 1000; // 3s
    // 小于request 肯定不会启动熔断
    protected  long requestVolumeThreshold = 80;



}
