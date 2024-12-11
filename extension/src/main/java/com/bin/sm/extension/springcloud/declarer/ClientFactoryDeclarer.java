package com.bin.sm.extension.springcloud.declarer;

import com.bin.sm.extension.springcloud.interceptor.ClientFactoryInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractDeclarer;

public class ClientFactoryDeclarer extends AbstractDeclarer {
    private static final String ENHANCE_CLASS =
            "org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory";


    private static final String METHOD_NAME = "getInstance";

    /**
     * 构造方法
     */
    public ClientFactoryDeclarer() {
        super(ENHANCE_CLASS, new ClientFactoryInterceptor(), METHOD_NAME);
    }
}
