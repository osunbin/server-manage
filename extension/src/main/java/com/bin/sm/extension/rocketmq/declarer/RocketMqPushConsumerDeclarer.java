package com.bin.sm.extension.rocketmq.declarer;

import com.bin.sm.extension.rocketmq.RocketMqEnhancementHelper;
import com.bin.sm.plugin.agent.declarer.AbstractPluginDeclarer;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.matcher.ClassMatcher;

public class RocketMqPushConsumerDeclarer extends AbstractPluginDeclarer {
    @Override
    public ClassMatcher getClassMatcher() {
        return RocketMqEnhancementHelper.getPushConsumerClassMatcher();
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                RocketMqEnhancementHelper.getPushConsumerStartInterceptDeclarers(),
                RocketMqEnhancementHelper.getPushConsumerShutdownInterceptDeclarers()
        };
    }
}