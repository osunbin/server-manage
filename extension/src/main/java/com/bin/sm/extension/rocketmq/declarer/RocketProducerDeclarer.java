package com.bin.sm.extension.rocketmq.declarer;

import com.bin.sm.extension.rocketmq.interceptor.RocketProducerInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class RocketProducerDeclarer extends AbstractPluginDeclarer {
    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals("org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl");
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
         // sendDefaultImpl
        return new InterceptDeclarer[] {
                InterceptDeclarer.build(
                        (MethodMatcher.nameEquals("sendKernelImpl")
                        .and(MethodMatcher.paramTypesEqual("org.apache.rocketmq.common.message.Message",
                                "org.apache.rocketmq.common.message.MessageQueue",
                                "org.apache.rocketmq.client.impl.CommunicationMode",
                                "org.apache.rocketmq.client.producer,SendCallback",
                                "org.apache.rocketmq.client.impl.producer.TopicPublishInfo",
                                "java.lang.Long"))
                        .or(MethodMatcher.nameEquals("sendDefaultImpl")
                                .and(MethodMatcher.paramTypesEqual("org.apache.rocketmq.common.message.Message",
                                        "org.apache.rocketmq.client.impl.CommunicationMode",
                                        "org.apache.rocketmq.client.producer,SendCallback",
                                        "org.apache.rocketmq.client.impl.producer.TopicPublishInfo",
                                        "java.lang.Long")))),
                    new RocketProducerInterceptor())
        };
    }
}
