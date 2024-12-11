package com.bin.sm.extension.registry.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


public class RegistryDelayInterceptor extends AbstractInterceptor {

    private final AtomicBoolean isDelayed = new AtomicBoolean();

    private static final Consumer<Long> SLEEP = time -> {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
        }
    };

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        if (isDelayed.compareAndSet(false, true)) {
            SLEEP.accept(1000L);
        }
        return context;
    }
}
