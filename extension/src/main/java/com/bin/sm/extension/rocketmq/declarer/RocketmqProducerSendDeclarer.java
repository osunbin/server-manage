package com.bin.sm.extension.rocketmq.declarer;

import com.bin.sm.extension.rocketmq.interceptor.RocketmqProducerSendInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class RocketmqProducerSendDeclarer extends AbstractPluginDeclarer {
    private static final String ENHANCE_CLASS = "org.apache.rocketmq.client.impl.MQClientAPIImpl";

    private static final String METHOD_NAME = "sendMessage";

    private static final String[] METHOD_PARAM_TYPES = {
            "java.lang.String",
            "java.lang.String",
            "org.apache.rocketmq.common.message.Message",
            "org.apache.rocketmq.common.protocol.header.SendMessageRequestHeader",
            "long",
            "org.apache.rocketmq.client.impl.CommunicationMode",
            "org.apache.rocketmq.client.producer.SendCallback",
            "org.apache.rocketmq.client.impl.producer.TopicPublishInfo",
            "org.apache.rocketmq.client.impl.factory.MQClientInstance",
            "int",
            "org.apache.rocketmq.client.hook.SendMessageContext",
            "org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl"
    };

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals(ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.nameEquals(METHOD_NAME)
                        .and(MethodMatcher.paramTypesEqual(METHOD_PARAM_TYPES)), new RocketmqProducerSendInterceptor())
        };
    }
}
