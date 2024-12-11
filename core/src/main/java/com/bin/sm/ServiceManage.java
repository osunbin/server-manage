package com.bin.sm;

import com.bin.sm.internal.collection.CopyOnWriteMap;
import com.bin.sm.ratelimiter.concurrency.Limiter;

import java.util.Map;

/**
 *
 *
 *  延迟消息
 *  分布式事务
 *
 *
 */
public class ServiceManage {


    // 服务方法 限流
    private Map<String, Limiter> limiters = new CopyOnWriteMap<>();





}
