package com.bin.sm.extension.openfeign.declarer;

import com.bin.sm.extension.openfeign.interceptor.OpenFeignClientInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class OpenFeignClientDeclarer extends AbstractPluginDeclarer {
    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals("feign.Client$Default");
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[] {
                InterceptDeclarer.build(MethodMatcher.nameEquals("execute"),
                        new OpenFeignClientInterceptor())
        };

    }
}
