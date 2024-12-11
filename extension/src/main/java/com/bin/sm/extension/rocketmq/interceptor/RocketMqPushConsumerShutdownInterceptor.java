package com.bin.sm.extension.rocketmq.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

public class RocketMqPushConsumerShutdownInterceptor extends AbstractInterceptor {


    @Override
    public ExecuteContext before(ExecuteContext context) {
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
       // RocketMqPushConsumerController.removePushConsumer((DefaultMQPushConsumer) context.getObject());
        return context;
    }

}