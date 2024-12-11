package com.bin.sm.extension.redis.declarer;

import com.bin.sm.extension.redis.interceptor.JedisInterceptor;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;
import com.bin.sm.plugin.agent.matcher.MethodMatcher;

public class JedisDeclarer extends AbstractPluginDeclarer {


    // redis.clients.jedis.JedisPool/redis.clients.jedis.JedisSentinelPool   returnResource(redis.clients.jedis.Jedis)  归还


    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals("redis.clients.jedis.Connection");
    }
    // executeCommand(redis.clients.jedis.CommandObject)  字节数量
    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[] {
                InterceptDeclarer.build(MethodMatcher.nameEquals("executeCommand").and(MethodMatcher.paramTypesEqual("redis.clients.jedis.CommandObject")),
                        new JedisInterceptor())
        };
    }

}
