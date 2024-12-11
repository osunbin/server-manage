package com.bin.sm.extension.threadpool.interceptor;

import com.bin.sm.executor.ExtensibleThreadPoolExecutor;
import com.bin.sm.executor.dynamic.DynamicThreadPool;
import com.bin.sm.executor.support.ThreadPoolBuilder;
import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;


public class ThreadPoolBuilderInterceptor extends AbstractInterceptor {


    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        ThreadPoolBuilder builder =
                (ThreadPoolBuilder) context.getArguments()[0];
        ExtensibleThreadPoolExecutor executor = DynamicThreadPool.build(builder);
        context.changeResult(executor);
        return context;
    }
}
