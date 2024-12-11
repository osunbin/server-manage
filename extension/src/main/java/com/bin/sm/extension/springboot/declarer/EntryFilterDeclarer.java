package com.bin.sm.extension.springboot.declarer;

import com.bin.sm.extension.springboot.interceptor.EntryFilterInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class EntryFilterDeclarer extends AbstractPluginDeclarer {

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals("com.bin.sm.toolkit.EntryFilter");
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.nameEquals("doFilter")
                        .and(MethodMatcher.paramCountEquals(3)),
                        new EntryFilterInterceptor())
        };
    }
}
