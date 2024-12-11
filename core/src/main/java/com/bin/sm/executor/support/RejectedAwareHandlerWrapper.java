package com.bin.sm.executor.support;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RejectedAwareHandlerWrapper implements RejectedExecutionHandler {

    private RejectedExecutionHandler handler;


    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        handler.rejectedExecution(r, executor);
    }
}
