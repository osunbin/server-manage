package com.bin.sm.extension.rocketmq.interceptor;

import com.bin.sm.extension.rocketmq.wrapper.MessageListenerConcurrentlyWrapper;
import com.bin.sm.extension.rocketmq.wrapper.MessageListenerOrderlyWrapper;
import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;

/**
 *  监控服务器当前正在处理的任务、正在处理的mq
 */
public class RocketMessageListenerInterceptor extends AbstractInterceptor {
    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        Object argument = context.getArguments()[0];
        if (argument instanceof MessageListenerConcurrently) {
            MessageListenerConcurrentlyWrapper wrapper = new MessageListenerConcurrentlyWrapper((MessageListenerConcurrently) argument);
            context.changeArgs(new Object[]{wrapper});
        } else if (argument instanceof MessageListenerOrderly) {
            MessageListenerOrderlyWrapper wrapper = new MessageListenerOrderlyWrapper((MessageListenerOrderly) argument);
            context.changeArgs(new Object[]{wrapper});
        }

        return context;
    }
}
