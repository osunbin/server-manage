package com.bin.sm.executor.support;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

public enum RejectedPolicyTypeEnum {


    CALLER_RUNS_POLICY(1, "CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy()),


    ABORT_POLICY(2, "AbortPolicy", new ThreadPoolExecutor.AbortPolicy()),


    DISCARD_POLICY(3, "DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy()),


    DISCARD_OLDEST_POLICY(4, "DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy());



    private Integer type;


    private String name;

    private RejectedExecutionHandler rejectedHandler;

    RejectedPolicyTypeEnum(Integer type, String name, RejectedExecutionHandler rejectedHandler) {
        this.type = type;
        this.name = name;
        this.rejectedHandler = rejectedHandler;
    }



    public static RejectedExecutionHandler createPolicy(String name) {
        RejectedPolicyTypeEnum rejectedTypeEnum = Stream.of(RejectedPolicyTypeEnum.values())
                .filter(each -> Objects.equals(each.name, name))
                .findFirst()
                .orElse(null);
        if (rejectedTypeEnum != null) {
            return rejectedTypeEnum.rejectedHandler;
        }
        return ABORT_POLICY.rejectedHandler;
    }


    public static RejectedExecutionHandler createPolicy(int type) {
        Optional<RejectedExecutionHandler> rejectedTypeEnum = Stream.of(RejectedPolicyTypeEnum.values())
                .filter(each -> Objects.equals(type, each.type))
                .map(each -> each.rejectedHandler)
                .findFirst();
        RejectedExecutionHandler resultRejected = rejectedTypeEnum.orElseGet(() -> ABORT_POLICY.rejectedHandler);
        return resultRejected;
    }


    public static String getRejectedNameByType(int type) {
        return createPolicy(type).getClass().getSimpleName();
    }


    public static RejectedPolicyTypeEnum getRejectedPolicyTypeEnumByName(String name) {
        Optional<RejectedPolicyTypeEnum> rejectedTypeEnum = Stream.of(RejectedPolicyTypeEnum.values())
                .filter(each -> each.name.equals(name))
                .findFirst();
        return rejectedTypeEnum.orElse(ABORT_POLICY);
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public RejectedExecutionHandler getRejectedHandler() {
        return rejectedHandler;
    }
}