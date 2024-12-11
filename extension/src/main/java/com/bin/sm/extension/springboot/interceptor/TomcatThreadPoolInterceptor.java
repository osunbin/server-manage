package com.bin.sm.extension.springboot.interceptor;

import com.bin.sm.extension.springboot.TimeRunnable;
import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;

public class TomcatThreadPoolInterceptor extends AbstractInterceptor {
    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        Runnable runnable = (Runnable) context.getArguments()[0];
        context.changeArgs(new Object[]{new TimeRunnable(runnable)});
        return context;
    }
}
