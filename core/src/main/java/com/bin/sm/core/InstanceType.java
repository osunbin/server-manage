package com.bin.sm.core;

public enum InstanceType {

    APP(1,"app"),
    GROUP(2,"group"),
    FUNC(3,"func");

    private int code;
    private String name;
    InstanceType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
