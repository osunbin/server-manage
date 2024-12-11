package com.bin.sm.extension.hikari.declarer;

import com.bin.sm.extension.hikari.interceptor.HikariInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class HikariDeclarer extends AbstractPluginDeclarer {
    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals("com.zaxxer.hikari.pool.HikariPool");
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        MethodMatcher methodMatcher = MethodMatcher.isConstructor().and(MethodMatcher.paramTypesEqual("com.zaxxer.hikari.HikariConfig"));
        return new InterceptDeclarer[] {
                InterceptDeclarer.build(methodMatcher, new HikariInterceptor())
        };
    }
}
