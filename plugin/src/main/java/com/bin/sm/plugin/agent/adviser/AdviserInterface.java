package com.bin.sm.plugin.agent.adviser;

import com.bin.sm.plugin.agent.ExecuteContext;

public interface AdviserInterface {
    /**
     * The preceding trigger point of method
     *
     * @param context execute Context
     * @param adviceKey enhanced class name
     * @return ExecuteContext
     * @throws Throwable Throwable
     */
    ExecuteContext onMethodEnter(ExecuteContext context, String adviceKey) throws Throwable;

    /**
     * The post trigger point of method
     *
     * @param context execute Context
     * @param adviceKey enhanced class name
     * @return ExecuteContext
     * @throws Throwable Throwable
     */
    ExecuteContext onMethodExit(ExecuteContext context, String adviceKey) throws Throwable;
}
