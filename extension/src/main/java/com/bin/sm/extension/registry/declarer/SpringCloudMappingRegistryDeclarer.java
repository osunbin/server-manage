package com.bin.sm.extension.registry.declarer;

import com.bin.sm.extension.registry.interceptor.SpringCloudMappingRegistryInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class SpringCloudMappingRegistryDeclarer extends AbstractPluginDeclarer {
    private static final String ENHANCE_CLASS = "org.springframework.web.servlet.handler.AbstractHandlerMethodMapping";


    private static final String[] METHOD_NAME = {"registerMapping", "registerHandlerMethod"};

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.isExtendedFrom(ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.nameContains(METHOD_NAME), new SpringCloudMappingRegistryInterceptor())};
    }
}
