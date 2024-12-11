package com.bin.sm.extension.scheduled.declarer;

import com.bin.sm.extension.scheduled.interceptor.XxlJobExecuteInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class XxlJobExecuteDeclarer extends AbstractPluginDeclarer {
    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals("com.xxl.job.core.handler.impl.MethodJobHandler");
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[] {
                InterceptDeclarer.build(MethodMatcher.nameEquals("execute"),new XxlJobExecuteInterceptor())
        };
    }
}
