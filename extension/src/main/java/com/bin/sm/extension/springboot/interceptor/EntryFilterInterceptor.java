package com.bin.sm.extension.springboot.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;

public class EntryFilterInterceptor extends AbstractInterceptor {



    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        Thread currentThread = Thread.currentThread();


       // FutureTask<?> f = (FutureTask<?>) executor.submit(sa);
        return context;
    }
}
