package com.bin.sm.extension.rocketmq;

import com.bin.sm.extension.rocketmq.interceptor.RocketMqPushConsumerShutdownInterceptor;
import com.bin.sm.extension.rocketmq.interceptor.RocketMqPushConsumerStartInterceptor;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class RocketMqEnhancementHelper {
    private static final String ENHANCE_PUSH_CONSUMER_CLASS =
            "org.apache.rocketmq.client.consumer.DefaultMQPushConsumer";


    private static final String START_METHOD_NAME = "start";

    private static final String SHUTDOWN_METHOD_NAME = "shutdown";

    private static final String SUBSCRIBE_METHOD_NAME = "subscribe";

    private static final String UNSUBSCRIBE_METHOD_NAME = "unsubscribe";

    private static final String ASSIGN_METHOD_NAME = "assign";

    private RocketMqEnhancementHelper() {
    }

    /**
     * Obtain ClassMatcher for pushConsumer interception point
     *
     * @return Return classMatcher
     */
    public static ClassMatcher getPushConsumerClassMatcher() {
        return ClassMatcher.nameEquals(ENHANCE_PUSH_CONSUMER_CLASS);
    }



    /**
     * Obtain the interception declarer for the start method of pushconsumer interception point
     *
     * @return return to the interception declarer
     */
    public static InterceptDeclarer getPushConsumerStartInterceptDeclarers() {
        return InterceptDeclarer.build(getStartMethodMatcher(), new RocketMqPushConsumerStartInterceptor());
    }



    /**
     * Obtain the interception declarer for the shutdown method of pushconsumer interception point
     *
     * @return return to the interception declarer
     */
    public static InterceptDeclarer getPushConsumerShutdownInterceptDeclarers() {
        return InterceptDeclarer.build(getShutdownMethodMatcher(), new RocketMqPushConsumerShutdownInterceptor());
    }




    /**
     * Obtain the method matcher intercepted by the start method
     *
     * @return method matcher
     */
    private static MethodMatcher getStartMethodMatcher() {
        return MethodMatcher.nameEquals(START_METHOD_NAME);
    }

    /**
     * Obtain the method matcher intercepted by the shutdown method
     *
     * @return method matcher
     */
    private static MethodMatcher getShutdownMethodMatcher() {
        return MethodMatcher.nameEquals(SHUTDOWN_METHOD_NAME);
    }

    /**
     * Obtain the method matcher intercepted by the subscribe method
     *
     * @return method matcher
     */
    private static MethodMatcher getSubscribeMethodMatcher() {
        return MethodMatcher.nameEquals(SUBSCRIBE_METHOD_NAME);
    }

    /**
     * Obtain the method matcher intercepted by the unsubscribe method
     *
     * @return method matcher
     */
    private static MethodMatcher getUnsubscribeMethodMatcher() {
        return MethodMatcher.nameEquals(UNSUBSCRIBE_METHOD_NAME);
    }

    /**
     * Obtain the method matcher intercepted by the assign method
     *
     * @return method matcher
     */
    private static MethodMatcher getAssignMethodMatcher() {
        return MethodMatcher.nameEquals(ASSIGN_METHOD_NAME);
    }
}
