package com.bin.sm.extension.springcloud.interceptor;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.interceptor.AbstractInterceptor;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;

public class ClientFactoryInterceptor extends AbstractInterceptor {

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        String serviceId = (String) context.getArguments()[0];
        return context;
    }



}
