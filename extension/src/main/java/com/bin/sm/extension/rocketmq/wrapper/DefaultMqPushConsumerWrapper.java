package com.bin.sm.extension.rocketmq.wrapper;

import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;

public class DefaultMqPushConsumerWrapper extends AbstractConsumerWrapper {
    private final DefaultMQPushConsumer pushConsumer;

    private final DefaultMQPushConsumerImpl pushConsumerImpl;

    /**
     * parameter construction method
     *
     * @param consumer push consumers
     * @param pushConsumerImpl Push consumers internally
     * @param clientFactory rocketmq client factory class
     */
    public DefaultMqPushConsumerWrapper(DefaultMQPushConsumer consumer, DefaultMQPushConsumerImpl pushConsumerImpl,
            MQClientInstance clientFactory) {
        super(clientFactory);
        this.pushConsumer = consumer;
        this.pushConsumerImpl = pushConsumerImpl;
        initPushClientInfo();
    }

    private void initPushClientInfo() {
        ClientConfig clientConfig = clientFactory.getClientConfig();
        nameServerAddress = clientConfig.getClientIP();
        clientIp = clientConfig.getClientIP();
        instanceName = clientConfig.getInstanceName();
        consumerGroup = pushConsumer.getConsumerGroup();
    }

    public DefaultMQPushConsumer getPushConsumer() {
        return pushConsumer;
    }

    public DefaultMQPushConsumerImpl getPushConsumerImpl() {
        return pushConsumerImpl;
    }
}