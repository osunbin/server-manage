package com.bin.sm.extension.mybatis.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;

import java.lang.reflect.Method;

/**
 *  sql监控
 */
public class MybatisInterceptor extends AbstractInterceptor {

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        Object[] arguments = context.getArguments();
        Object proxy = arguments[0];
        Method method = (Method) arguments[1];

        return context;
    }
}
