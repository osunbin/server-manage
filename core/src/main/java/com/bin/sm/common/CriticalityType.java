package com.bin.sm.common;

public enum CriticalityType {
    CRITICAL_PLUS(1,"criticalPlus","最重要-为最终的要求预留的类型，拒绝这些请求会造成非常严重的用户可见的问题"),
    CRITICAL(2,"critical","重要-生产任务发出的默认请求类型。拒绝这些请求也会造成用户可见的问题。但是可能没那么严重"),
    SHEDDABLE_PLUS(3,"sheddablePlus","可丢弃的-这些流量可以容忍某种程度的不可用性。这是批量任务发出的请求的默认值。这些请求通常可以过几分钟、几小时后重试"),
    SHEDDABLE(4,"sheddable","可丢弃的-这些流量可能会经常遇到部分不可用情况，偶尔会完全不可用");


    private int code;
    private final String name;

    private final String desc;

    CriticalityType(int code,String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }


    public int code() {
        return code;
    }

    public String names() {
        return name;
    }

    public String desc() {
        return desc;
    }
}
