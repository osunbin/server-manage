package com.bin.sm.extension.rocketmq.declarer;

import com.bin.sm.extension.rocketmq.interceptor.RocketMessageListenerInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class RocketMessageListenerDeclarer extends AbstractPluginDeclarer {

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals("org.apache.rocketmq.client.consumer.DefaultMQPushConsumer");
    }

    /**
     *

     */
    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {

        return new InterceptDeclarer[] {

                InterceptDeclarer.build(
                        (MethodMatcher.nameEquals("registerMessageListener")
                                .and(MethodMatcher.paramTypesEqual("org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently")))
                                .or(MethodMatcher.nameEquals("registerMessageListener")
                                        .and(MethodMatcher.paramTypesEqual("org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly"))),
                        new RocketMessageListenerInterceptor())
        };
    }
}
