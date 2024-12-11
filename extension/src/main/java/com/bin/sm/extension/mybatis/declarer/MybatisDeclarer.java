package com.bin.sm.extension.mybatis.declarer;

import com.bin.sm.extension.mybatis.interceptor.MybatisInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

/**
 *  sql 执行时间 qps
 */
public class MybatisDeclarer extends AbstractPluginDeclarer {

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals("org.apache.ibatis.binding.MapperProxy");
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[] {
                InterceptDeclarer.build(MethodMatcher.nameEquals("invoke"),
                        new MybatisInterceptor())
        };
    }
}
