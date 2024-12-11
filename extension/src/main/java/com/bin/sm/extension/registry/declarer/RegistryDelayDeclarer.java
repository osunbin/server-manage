package com.bin.sm.extension.registry.declarer;

import com.bin.sm.extension.registry.interceptor.RegistryDelayInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class RegistryDelayDeclarer extends AbstractPluginDeclarer {
    /**
     * Older versions 1.5.x(springboot)
     */
    public static final String OLD_VERSION_ENHANCE_CLASS =
            "org.springframework.cloud.client.discovery.AbstractDiscoveryLifecycle";

    /**
     * EUREKA Automatic registration class
     */
    private static final String EUREKA_ENHANCE_CLASS =
            "org.springframework.cloud.netflix.eureka.serviceregistry.EurekaAutoServiceRegistration";

    /**
     * Universal automatic registration class
     */
    private static final String ENHANCE_CLASS =
            "org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration";



    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameContains(ENHANCE_CLASS, EUREKA_ENHANCE_CLASS,
                OLD_VERSION_ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
            InterceptDeclarer.build(MethodMatcher.nameEquals("start"), new RegistryDelayInterceptor())
        };
    }
}
