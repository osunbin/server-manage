package com.bin.sm.circuitbreak;

import com.bin.sm.context.NodeInstance;

public interface CircuitBreaker {

    enum Status {
        CLOSED, OPEN, HALF_OPEN;
    }


    NodeInstance nodeInstance();

    CircuitBreakerType circuitBreakerType();

    boolean allow();

    void markSuccess();

    void markFailed();

    void markTimeout();
}
