package com.bin.sm.plugin.agent.declarer;

import com.bin.sm.plugin.agent.interceptor.Interceptor;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public abstract class AbstractDeclarer extends AbstractPluginDeclarer {
    private final String enhanceClass;

    private final Interceptor interceptClass;

    private final String methodName;

    /**
     * constructionMethod
     *
     * @param enhanceClass enhancement class
     * @param interceptClass interceptor class
     * @param methodName enhancement method
     */
    public AbstractDeclarer(String enhanceClass, Interceptor interceptClass, String methodName) {
        this.enhanceClass = enhanceClass;
        this.interceptClass = interceptClass;
        this.methodName = methodName;
    }

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals(enhanceClass);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(getMethodMatcher(), interceptClass)
        };
    }

    /**
     * get method matcher
     *
     * @return method matcher
     */
    public MethodMatcher getMethodMatcher() {
        return MethodMatcher.nameEquals(methodName);
    }
}
