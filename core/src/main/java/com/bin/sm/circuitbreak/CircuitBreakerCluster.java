package com.bin.sm.circuitbreak;

import java.util.Map;

public class CircuitBreakerCluster {

    private String resource;
    // address -> CircuitBreaker
    private Map<String,CircuitBreaker> circuitBreakers;


    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Map<String, CircuitBreaker> getCircuitBreakers() {
        return circuitBreakers;
    }

    public void setCircuitBreakers(Map<String, CircuitBreaker> circuitBreakers) {
        this.circuitBreakers = circuitBreakers;
    }
}
