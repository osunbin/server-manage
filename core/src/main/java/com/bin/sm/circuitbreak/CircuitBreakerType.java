package com.bin.sm.circuitbreak;

public enum CircuitBreakerType {

    DEFAULT(1,"default"),
    SRE(2,"sre");

    private int code;
    private String name;
    CircuitBreakerType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int code() {
        return code;
    }

    public String getName() {
        return name;
    }
}
