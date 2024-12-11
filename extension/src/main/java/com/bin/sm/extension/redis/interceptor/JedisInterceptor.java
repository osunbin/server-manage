package com.bin.sm.extension.redis.interceptor;

import com.bin.sm.extension.common.SystemClock;
import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import redis.clients.jedis.CommandObject;
import redis.clients.jedis.args.Rawable;

/**
 *  执行 方法 指令 执行时间  内容大小
 */
public class JedisInterceptor extends AbstractInterceptor {



    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        context.setLocalFieldValue("startTime", SystemClock.now());


        CommandObject commandObject = (CommandObject) context.getArguments()[0];
        long byteSize = 0;
        for (Rawable raw : commandObject.getArguments()) {
            byteSize += raw.getRaw().length;
        }

        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        long start = (long) context.removeLocalFieldValue("startTime");
        
        return context;
    }
}
