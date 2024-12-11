package com.bin.sm.plugin.agent.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;

public abstract class AbstractInterceptor implements Interceptor{


    @Override
    public ExecuteContext onThrow(ExecuteContext context) throws Exception {
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        return context;
    }

}
