package com.bin.sm.extension.threadpool.declarer;

import com.bin.sm.extension.threadpool.interceptor.ThreadPoolBuilderInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class ThreadPoolBuilderDeclarer extends AbstractPluginDeclarer {


    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals("com.bin.sm.executor.support.ThreadPoolBuilder");
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.nameEquals("build")
                        .and(MethodMatcher.paramTypesEqual("com.bin.sm.executor.support.ThreadPoolBuilder")),
                         new ThreadPoolBuilderInterceptor())
        };
    }
}
