package com.bin.sm.common;

public enum FluxType {
    /**
     * 访问量
     */
    Normal("访问量"),
    /**
     * 
     */
    Drop("抛弃量"),
    /**
     * 超时量
     */
    TimeOut("超时量"),
    /**
     * 异常量
     */
    Exception("异常量"),
    /**
     * 拒绝量
     */
    Reject("拒绝量"),
    /**
     * 限流量
     */
    Limit("限流量"),
    /**
     * 熔断量
     */
    FallBack("熔断量");
    
    private String value;
    
    FluxType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String getMainValue() {
        return this.value.replace("量", "");
    }
    

    
    public static FluxType parse(int type) {
        if (type == 1) {
            return Normal;
        }
        if (type == 3) {
            return Drop;
        }
        if (type == 2) {
            return Exception;
        }
        if (type == 4) {
            return TimeOut;
        }
        if (type == 5) {
            return Reject;
        }
        if (type == 7) {
            return Limit;
        }
        if (type == 8) {
            return FallBack;
        }
        throw new RuntimeException("no type " + type);
    }
    
}