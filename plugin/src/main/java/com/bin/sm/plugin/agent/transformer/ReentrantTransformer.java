package com.bin.sm.plugin.agent.transformer;

import com.bin.sm.plugin.Plugin;

import com.bin.sm.plugin.agent.EnhancementManager;
import com.bin.sm.plugin.agent.adviser.AdviserScheduler;
import com.bin.sm.plugin.agent.declarer.InterceptDeclarer;
import com.bin.sm.plugin.agent.interceptor.Interceptor;
import com.bin.sm.plugin.agent.template.BaseAdviseHandler;
import com.bin.sm.plugin.agent.template.MethodKeyCreator;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription.InDefinedShape;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReentrantTransformer extends AbstractTransformer {
    private final Plugin plugin;

    /**
     * constructor
     *
     * @param interceptDeclarers intercept declarer set
     * @param plugin belonged plugin
     */
    public ReentrantTransformer(InterceptDeclarer[] interceptDeclarers, Plugin plugin) {
        super(interceptDeclarers);
        this.plugin = plugin;
    }

    @Override
    protected Builder<?> resolve(Builder<?> builder, InDefinedShape methodDesc, List<Interceptor> interceptors,
            Class<?> templateCls, ClassLoader classLoader)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        final String adviceKey = getAdviceKey(templateCls, classLoader, methodDesc);
        List<Interceptor> interceptorsForAdviceKey = BaseAdviseHandler.getInterceptorListMap()
                .computeIfAbsent(adviceKey, key -> new ArrayList<>());
        Set<String> createdInterceptorForAdviceKey = plugin.getInterceptors()
                .computeIfAbsent(adviceKey, key -> new HashSet<>());
        for (Interceptor interceptor : interceptors) {
            // need to check whether the Interceptor is created
            if (checkInterceptor(adviceKey, interceptor.getClass().getCanonicalName())) {
                interceptorsForAdviceKey.add(interceptor);
                createdInterceptorForAdviceKey.add(interceptor.getClass().getCanonicalName());
            }
        }
        EnhancementManager.addEnhancements(plugin, interceptors, classLoader,
                MethodKeyCreator.getMethodDescKey(methodDesc));
        if (checkAdviceLock(adviceKey)) {
            return builder.visit(Advice.to(templateCls).on(ElementMatchers.is(methodDesc)));
        }
        return builder;
    }

    private boolean checkAdviceLock(String adviceKey) {
        if (AdviserScheduler.lock(adviceKey)) {
            // adviceKey lock is successfully obtained, then manage it in the plugin
            plugin.getAdviceLocks().add(adviceKey);
            return true;
        }
        return plugin.getAdviceLocks().contains(adviceKey);
    }

    private boolean checkInterceptor(String adviceKey, String interceptor) {
        // Whether the plugin has created an interceptor for the adviceKey
        return !plugin.getInterceptors().get(adviceKey).contains(interceptor);
    }
}