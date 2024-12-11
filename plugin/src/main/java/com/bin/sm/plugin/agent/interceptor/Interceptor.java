package com.bin.sm.plugin.agent.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;

public interface Interceptor {

    ExecuteContext before(ExecuteContext context) throws Exception;


    ExecuteContext after(ExecuteContext context) throws Exception;

    ExecuteContext onThrow(ExecuteContext context) throws Exception;

}
