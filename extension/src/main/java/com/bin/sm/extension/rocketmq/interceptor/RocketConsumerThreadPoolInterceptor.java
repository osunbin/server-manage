package com.bin.sm.extension.rocketmq.interceptor;

import com.bin.sm.executor.middleware.RocketMQThreadPool;
import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import com.bin.sm.plugin.utils.ReflectUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl;
import org.apache.rocketmq.client.impl.consumer.RebalanceImpl;
import org.apache.rocketmq.common.protocol.heartbeat.SubscriptionData;


import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadPoolExecutor;

public class RocketConsumerThreadPoolInterceptor extends AbstractInterceptor {


    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {

        DefaultMQPushConsumer pushConsumer = (DefaultMQPushConsumer) context.getObject();

        String consumerGroup = pushConsumer.getConsumerGroup();
        DefaultMQPushConsumerImpl defaultMQPushConsumerImpl = pushConsumer.getDefaultMQPushConsumerImpl();

        RebalanceImpl rebalance = (RebalanceImpl) ReflectUtil.getFieldValue(defaultMQPushConsumerImpl, "rebalanceImpl");
        ConcurrentMap<String, SubscriptionData> subscriptionInner = rebalance.getSubscriptionInner();
        StringBuilder threadPoolName = new StringBuilder(consumerGroup);

        subscriptionInner.values().forEach(action -> {
            threadPoolName.append("_")
                    .append(action.getTopic());

            String subString = action.getSubString();
            if (!"*".equals(subString)) {
                threadPoolName.append("-")
                        .append(action.getSubString());
            }

        });


        MessageListener messageListenerInner = defaultMQPushConsumerImpl.getMessageListenerInner();
        ThreadPoolExecutor consumeExecutor = (ThreadPoolExecutor) ReflectUtil.getFieldValue(messageListenerInner, "consumeExecutor");

        RocketMQThreadPool.registerThreadPool(threadPoolName.toString(), consumeExecutor);

        return context;
    }

}
