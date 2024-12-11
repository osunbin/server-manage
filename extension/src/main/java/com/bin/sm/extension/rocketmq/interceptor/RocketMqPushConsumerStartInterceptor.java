package com.bin.sm.extension.rocketmq.interceptor;

import com.bin.sm.extension.rocketmq.RocketMqPushConsumer;
import com.bin.sm.extension.rocketmq.wrapper.DefaultMqPushConsumerWrapper;
import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

public class RocketMqPushConsumerStartInterceptor extends AbstractInterceptor {

    @Override
    public ExecuteContext before(ExecuteContext context) {
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        DefaultMQPushConsumer pushConsumer = (DefaultMQPushConsumer) context.getObject();
        RocketMqPushConsumer.cachePushConsumer(pushConsumer);

        DefaultMqPushConsumerWrapper pushConsumerWrapper =
                RocketMqPushConsumer.getPushConsumerWrapper(pushConsumer);
        if (pushConsumerWrapper != null) {
            pushConsumerWrapper.setSubscribedTopics(pushConsumerWrapper.getPushConsumerImpl()
                    .getSubscriptionInner().keySet());
        }
        return context;
    }
}
