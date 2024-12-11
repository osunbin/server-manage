package com.bin.sm.extension.openfeign.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import feign.Request;


public class OpenFeignClientInterceptor extends AbstractInterceptor {


    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        Object[] arguments = context.getArguments();

        Request request = (Request) arguments[0];
        Request.Options options = (Request.Options) arguments[1];


        return context;
    }
}
