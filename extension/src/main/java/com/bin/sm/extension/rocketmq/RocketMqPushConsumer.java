package com.bin.sm.extension.rocketmq;

import com.bin.sm.extension.rocketmq.wrapper.AbstractConsumerWrapper;
import com.bin.sm.extension.rocketmq.wrapper.DefaultMqPushConsumerWrapper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RocketMqPushConsumer {


    public static final Map<Integer, DefaultMqPushConsumerWrapper> PUSH_CONSUMERS_CACHE =
            new ConcurrentHashMap<>();


    public static DefaultMqPushConsumerWrapper getPushConsumerWrapper(DefaultMQPushConsumer pushConsumer) {
        return PUSH_CONSUMERS_CACHE.get(pushConsumer.hashCode());
    }


    public static void cachePushConsumer(DefaultMQPushConsumer pushConsumer) {
        Optional<DefaultMqPushConsumerWrapper> pushConsumerWrapperOptional =  wrapPushConsumer(pushConsumer);
        pushConsumerWrapperOptional.ifPresent(defaultMqPushConsumerWrapper -> PUSH_CONSUMERS_CACHE.put(pushConsumer.hashCode(), defaultMqPushConsumerWrapper));
    }



    private static void suspendPushConsumer(DefaultMqPushConsumerWrapper wrapper) {
        if (wrapper.isProhibition()) {
//            LOGGER.log(Level.INFO, "Consumer has prohibited consumption, consumer instance name : {0}, "
//                            + "consumer group : {1}, topic : {2}",
//                    new Object[]{wrapper.getInstanceName(), wrapper.getConsumerGroup(), wrapper.getSubscribedTopics()});
//
            return;
        }

        DefaultMQPushConsumerImpl pushConsumerImpl = wrapper.getPushConsumerImpl();
        String consumerGroup = wrapper.getConsumerGroup();

        // Before exiting the consumer group, actively submit the offset for consumption, and immediately trigger
        // a rebalancing and reassign the queue after exiting the consumer group
        pushConsumerImpl.persistConsumerOffset();
        //pushConsumerImpl.unsubscribe("");
        wrapper.getClientFactory().unregisterConsumer(consumerGroup);
        pushConsumerImpl.doRebalance();
        wrapper.setProhibition(true);

//        LOGGER.log(Level.INFO, "Success to prohibit consumption, consumer instance name : {0}, "
//                        + "consumer group : {1}, topic : {2}",
//                new Object[]{wrapper.getInstanceName(), consumerGroup, wrapper.getSubscribedTopics()});
//
    }

    private static void resumePushConsumer(DefaultMqPushConsumerWrapper wrapper) {
        String instanceName = wrapper.getInstanceName();
        String consumerGroup = wrapper.getConsumerGroup();
        Set<String> subscribedTopics = wrapper.getSubscribedTopics();
        if (!wrapper.isProhibition()) {
//            LOGGER.log(Level.INFO, "Consumer has opened consumption, consumer "
//                            + "instance name : {0}, consumer group : {1}, topic : {2}",
//                    new Object[]{instanceName, consumerGroup, subscribedTopics});
            return;
        }

        DefaultMQPushConsumerImpl pushConsumerImpl = wrapper.getPushConsumerImpl();
        wrapper.getClientFactory().registerConsumer(consumerGroup, pushConsumerImpl);
        pushConsumerImpl.doRebalance();
        wrapper.setProhibition(false);
//        LOGGER.log(Level.INFO, "Success to open consumption, consumer "
//                        + "instance name : {0}, consumer group : {1}, topic : {2}",
//                new Object[]{instanceName, consumerGroup, subscribedTopics});
    }




    private static Optional<DefaultMqPushConsumerWrapper> wrapPushConsumer(DefaultMQPushConsumer pushConsumer) {
        DefaultMQPushConsumerImpl pushConsumerImpl = pushConsumer.getDefaultMQPushConsumerImpl();
        MQClientInstance mqClientFactory = pushConsumerImpl.getmQClientFactory();

        // Obtain the defaultMQPushConsumerImpl and mQClientFactory attribute values related to the consumer. If the
        // attribute value is null, do not cache the consumer
        if (pushConsumerImpl != null && mqClientFactory != null) {
            DefaultMqPushConsumerWrapper wrapper = new DefaultMqPushConsumerWrapper(pushConsumer, pushConsumerImpl,
                    mqClientFactory);
            initWrapperServiceMeta(wrapper);
            return Optional.of(wrapper);
        }
        return Optional.empty();
    }

    private static void initWrapperServiceMeta(AbstractConsumerWrapper wrapper) {
//        ServiceMeta serviceMeta = ConfigManager.getConfig(ServiceMeta.class);
//        wrapper.setZone(serviceMeta.getZone());
//        wrapper.setProject(serviceMeta.getProject());
//        wrapper.setEnvironment(serviceMeta.getEnvironment());
//        wrapper.setApplication(serviceMeta.getApplication());
//        wrapper.setService(serviceMeta.getService());
    }

}
