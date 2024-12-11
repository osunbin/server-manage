package com.bin.sm.extension.springboot.declarer;

import com.bin.sm.extension.springboot.interceptor.TomcatThreadAfterExecuteInterceptor;
import com.bin.sm.extension.springboot.interceptor.TomcatThreadBeforeExecuteInterceptor;
import com.bin.sm.extension.springboot.interceptor.TomcatThreadPoolInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class TomcatThreadPoolDeclarer extends AbstractPluginDeclarer {
    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals("org.apache.tomcat.util.threads.ThreadPoolExecutor");
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {

        return new InterceptDeclarer[] {
                InterceptDeclarer.build(MethodMatcher.nameEquals("execute"),
                        new TomcatThreadPoolInterceptor()),
                InterceptDeclarer.build(MethodMatcher.nameEquals("beforeExecute"),
                        new TomcatThreadBeforeExecuteInterceptor()),
                InterceptDeclarer.build(MethodMatcher.nameEquals("afterExecute"),
                        new TomcatThreadAfterExecuteInterceptor())
        };
    }
}
