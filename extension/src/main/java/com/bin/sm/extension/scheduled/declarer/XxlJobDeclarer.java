package com.bin.sm.extension.scheduled.declarer;

import com.bin.sm.extension.scheduled.interceptor.XxlJobInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class XxlJobDeclarer extends AbstractPluginDeclarer {
    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals("com.xxl.job.core.biz.impl.ExecutorBizImpl");
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {

        return new InterceptDeclarer[] {
                InterceptDeclarer.build(MethodMatcher.nameEquals("run"),new XxlJobInterceptor())
        };
    }
}
