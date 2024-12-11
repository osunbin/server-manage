package com.bin.sm.ratelimiter;

import java.util.HashSet;
import java.util.Set;

public class ServiceRejectMeta {
    /**
     * 调用者密钥
     */
    private String callerName;

    /**
     * 是否拒绝
     */
    private boolean reject;

    /**
     * 是否调用
     */
    private boolean noCallerUsage;


    /**
     * 不拒绝函数列表
     */
    private Set<String> notRejectFunction = new HashSet<>();

    public String getCallerName() {
        return callerName;
    }

    public ServiceRejectMeta setCallerName(String callerName) {
        this.callerName = callerName;
        return this;
    }

    public boolean isReject() {
        return reject;
    }

    public ServiceRejectMeta setReject(boolean reject) {
        this.reject = reject;
        return this;
    }

    public boolean isNoCallerUsage() {
        return noCallerUsage;
    }

    public ServiceRejectMeta setNoCallerUsage(boolean noCallerUsage) {
        this.noCallerUsage = noCallerUsage;
        return this;
    }

    public Set<String> getNotRejectFunction() {
        return notRejectFunction;
    }

    public ServiceRejectMeta setNotRejectFunction(Set<String> notRejectFunction) {
        this.notRejectFunction = notRejectFunction;
        return this;
    }
}
