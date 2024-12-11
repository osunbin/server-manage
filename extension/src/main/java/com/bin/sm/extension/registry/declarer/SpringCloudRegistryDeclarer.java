package com.bin.sm.extension.registry.declarer;

import com.bin.sm.extension.registry.interceptor.SpringCloudRegistryInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class SpringCloudRegistryDeclarer extends AbstractPluginDeclarer {

    private static final String ENHANCE_CLASS = "org.springframework.cloud.client.serviceregistry.ServiceRegistry";

    private static final String METHOD_NAME = "register";

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.isExtendedFrom(ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{InterceptDeclarer.build(MethodMatcher.nameEquals(METHOD_NAME), new SpringCloudRegistryInterceptor())};
    }
}
