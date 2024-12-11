package com.bin.sm.extension.redis.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;

/**
 *  计算当前使用的 连接
 */
public class JedisPoolInterceptor extends AbstractInterceptor {
    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        return null;
    }
}
