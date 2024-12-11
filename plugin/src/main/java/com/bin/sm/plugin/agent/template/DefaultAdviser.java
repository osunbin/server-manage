package com.bin.sm.plugin.agent.template;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.adviser.AdviserInterface;
import com.bin.sm.plugin.agent.interceptor.Interceptor;
import com.bin.sm.plugin.common.LoggerFactory;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultAdviser implements AdviserInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    /**
     * Output error log
     *
     * @param scene scene
     * @param context ExecuteContext
     * @param interceptor Interceptor
     * @param throwable Throwable
     */
    private void logError(String scene, ExecuteContext context, Interceptor interceptor, Throwable throwable) {
        LOGGER.log(Level.SEVERE, String.format(Locale.ROOT, "An error occurred %s [%s] in interceptor [%s]: ", scene,
                MethodKeyCreator.getMethodKey(context.getMethod()), interceptor.getClass().getName()), throwable);
    }

    @Override
    public ExecuteContext onMethodEnter(ExecuteContext context, String adviceKey) throws Throwable {
        return BaseAdviseHandler.handleMethodEnter(context, adviceKey, new BaseAdviseHandler.ExceptionHandler() {
            @Override
            public void handle(ExecuteContext context, Interceptor interceptor, Throwable throwable) {
                logError("before executing", context, interceptor, throwable);
            }
        });
    }

    @Override
    public ExecuteContext onMethodExit(ExecuteContext context, String adviceKey) throws Throwable {
        return BaseAdviseHandler.handleMethodExit(context, adviceKey, new BaseAdviseHandler.ExceptionHandler() {
            @Override
            public void handle(ExecuteContext context, Interceptor interceptor, Throwable throwable) {
                logError("while handling error from", context, interceptor, throwable);
            }
        }, new BaseAdviseHandler.ExceptionHandler() {
            @Override
            public void handle(ExecuteContext context, Interceptor interceptor, Throwable throwable) {
                logError("after executing", context, interceptor, throwable);
            }
        });
    }
}
