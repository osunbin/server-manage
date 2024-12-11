package com.bin.sm.context;


import com.bin.sm.circuitbreak.CircuitBreaker;
import com.bin.sm.ratelimiter.concurrency.Limiter;

public interface NodeInstance {

    Instance instance();

    String getIp();

    String getAddress();


    Limiter getLimiter();


    CircuitBreaker getCircuitBreaker();


    void setLastUsed(long lastUsed);

    long getLastUsed();
}
