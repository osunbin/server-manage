package com.bin.sm.extension.rocketmq.wrapper;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class MessageListenerOrderlyWrapper implements MessageListenerOrderly {

    private MessageListenerOrderly target;

    public MessageListenerOrderlyWrapper(MessageListenerOrderly target) {
        this.target = target;
    }

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {

        ConsumeOrderlyStatus consumeOrderlyStatus = target.consumeMessage(list, consumeOrderlyContext);

        return consumeOrderlyStatus;
    }
}
