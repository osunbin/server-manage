package com.bin.sm.circuitbreak.support;

import com.bin.sm.circuitbreak.CircuitBreaker;

public interface CircuitBreakerStatusEvent {

    void openToClose(CircuitBreaker circuitBreaker);

    void closeToOpen(CircuitBreaker circuitBreaker,String reason);

}
