package com.bin.sm.extension.springboot.interceptor;

import com.bin.sm.extension.springboot.TimeRunnable;
import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;

public class TomcatThreadAfterExecuteInterceptor extends AbstractInterceptor {
    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        Runnable runnable = (Runnable) context.getArguments()[0];
        if (runnable instanceof TimeRunnable) {
            // 清除
        }
        return context;
    }
}
