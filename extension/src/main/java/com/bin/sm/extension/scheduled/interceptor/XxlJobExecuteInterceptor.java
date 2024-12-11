package com.bin.sm.extension.scheduled.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;

public class XxlJobExecuteInterceptor extends AbstractInterceptor {
    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        // 计数
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        // 计数
        return context;
    }

    @Override
    public ExecuteContext onThrow(ExecuteContext context) throws Exception {
        // 计数
        return context;
    }
}
