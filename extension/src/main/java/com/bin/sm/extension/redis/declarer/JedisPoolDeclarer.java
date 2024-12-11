package com.bin.sm.extension.redis.declarer;

import com.bin.sm.extension.redis.interceptor.JedisPoolInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class JedisPoolDeclarer extends AbstractPluginDeclarer {
    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameContains("redis.clients.jedis.JedisPool","redis.clients.jedis.JedisSentinelPool");
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.nameEquals("returnResource").and(MethodMatcher.paramTypesEqual("redis.clients.jedis.Jedis")),
                        new JedisPoolInterceptor())
        };
    }
}
