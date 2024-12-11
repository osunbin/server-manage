package com.bin.sm.plugin.agent.declarer;

import com.bin.sm.plugin.agent.matcher.ClassMatcher;

public interface PluginDeclarer {

    /**
     * Gets the class matcher for the plugin
     *
     * @return class matcher
     */
    ClassMatcher getClassMatcher();

    // 存在类  and  or   不存在
    default String[] conditionalOnClass() {
        return new String[0];
    }
    // 配置   and  or
    default String[] conditionalOnProperty() {
        return new String[0];
    }


    /**
     * Gets the plugin's InterceptDeclarers
     *
     * @param classLoader The classLoader of the enhanced class
     * @return InterceptDeclarer set
     */
    InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader);

    /**
     * Gets the superclass declarers for the plugin
     *
     * @return SuperTypeDeclarer set
     */
    SuperTypeDeclarer[] getSuperTypeDeclarers();

    /**
     * It is up to the plugin declarator to decide if the declared method needs to be enhanced. The default is TRUE
     *
     * @return result
     */
    default boolean isEnabled() {
        return true;
    }
}
