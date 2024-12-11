package com.bin.sm.plugin.agent.declarer;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;

/**
 *   抽象的插件描述器，简化类的匹配方法，仅使用类的描述器匹配
 */
public abstract class AbstractPluginDescription implements ElementMatcher<TypeDescription>, PluginDescription {
    @Override
    public boolean matches(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
            Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
        return matches(typeDescription);
    }
}