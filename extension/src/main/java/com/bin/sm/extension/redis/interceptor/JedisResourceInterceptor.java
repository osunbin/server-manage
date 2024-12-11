package com.bin.sm.extension.redis.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;

/**
 *   当前有多少连接
 */
public class JedisResourceInterceptor extends AbstractInterceptor {
    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        return context;
    }
}
