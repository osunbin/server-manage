package com.bin.sm.extension.springboot.interceptor;

import com.bin.sm.extension.springboot.TimeRunnable;
import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;

public class TomcatThreadBeforeExecuteInterceptor extends AbstractInterceptor {
    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        Runnable runnable = (Runnable) context.getArguments()[1];
        if (runnable instanceof TimeRunnable) {
            long startTime = ((TimeRunnable) runnable).getStartTime();

        }
        return context;
    }
}
