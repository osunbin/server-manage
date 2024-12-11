package com.bin.sm.extension.rocketmq.interceptor;

import com.bin.sm.context.RpcContext;
import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageBatch;


// 消息发送成功qps 消息发送失败qps 发送字节数量
public class RocketProducerInterceptor extends AbstractInterceptor {

    private static ThreadLocal<Boolean> SENDED = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static final String SENDED_FLAG = "sended";

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        if (SENDED.get()) {
            context.setLocalFieldValue(SENDED_FLAG, Boolean.TRUE);
            return context;
        }
        SENDED.set(Boolean.TRUE);

        Message message = (Message) context.getArguments()[0];
        long messageByte = 0;
        if (message instanceof MessageBatch messageBatch) {
            for (Message msg : messageBatch) {
                messageByte += msg.getBody().length;
            }
        } else {
            messageByte = message.getBody().length;
        }

        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        Object sended = context.getLocalFieldValue(SENDED_FLAG);
        if (sended != null && (Boolean) sended) {
            context.removeLocalFieldValue(SENDED_FLAG);
            return context;
        }
        SENDED.remove();

        Throwable throwableOut = context.getThrowableOut();
        if (throwableOut != null) {

        } else {

        }
        return context;
    }
}
