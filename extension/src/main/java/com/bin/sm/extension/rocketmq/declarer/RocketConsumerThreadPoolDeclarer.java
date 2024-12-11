package com.bin.sm.extension.rocketmq.declarer;

import com.bin.sm.extension.rocketmq.interceptor.RocketConsumerThreadPoolInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;
import com.bin.sm.plugin.utils.ReflectUtil;

import java.util.concurrent.ThreadPoolExecutor;

/**
 *  消费qps 消费time 消费失败量 消费字节数量  正在消费qps
 *     消费线程
 *
 *
 *
 *
 *
 */
public class RocketConsumerThreadPoolDeclarer extends AbstractPluginDeclarer {


    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameContains("org.apache.rocketmq.client.impl.consumer.ConsumeMessageConcurrentlyService",
                "org.apache.rocketmq.client.impl.consumer.ConsumeMessageOrderlyService");
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {

        return new InterceptDeclarer[] {
                InterceptDeclarer.build(MethodMatcher.nameEquals("start"),new RocketConsumerThreadPoolInterceptor())

        };
    }
}
