package com.bin.sm.extension.rocketmq.wrapper;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Map;

// 包装一下就知道 qps time
public class MessageListenerConcurrentlyWrapper implements MessageListenerConcurrently{

    private final MessageListenerConcurrently target;


    public MessageListenerConcurrentlyWrapper(MessageListenerConcurrently target) {
        this.target = target;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        MessageExt messageExt = list.getFirst();
        byte[] body = messageExt.getBody();
        // 参数
        Map<String, String> properties = messageExt.getProperties();

        ConsumeConcurrentlyStatus consumeConcurrentlyStatus = target.consumeMessage(list, consumeConcurrentlyContext);

        return consumeConcurrentlyStatus;
    }

}
