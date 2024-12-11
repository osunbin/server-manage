package com.bin.sm.context;

import com.bin.sm.circuitbreak.CircuitBreaker;
import com.bin.sm.ratelimiter.concurrency.Limiter;

import java.util.Objects;

public class DefaultNodeInstance implements NodeInstance {

    private String ip;
    private int port;

    private CircuitBreaker circuitBreaker;

    private Limiter limiter;

    private long lastUsed = System.currentTimeMillis();

    public DefaultNodeInstance(String ip,int port) {
        this.ip = ip;
        this.port = port;
    }

    public Instance instance() {
        return null;
    }

    public Limiter getLimiter() {
        return limiter;
    }

    public void setLimiter(Limiter limiter) {
        this.limiter = limiter;
    }

    public CircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }

    public void setCircuitBreaker(CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    public String getIp() {
        return ip;
    }

    public String getAddress() {
        return ip+":"+port;
    }


    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultNodeInstance that)) return false;
        return port == that.port && Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
