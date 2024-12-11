package com.bin.sm.plugin.agent;


import com.bin.sm.plugin.Plugin;
import com.bin.sm.plugin.agent.interceptor.Interceptor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnhancementManager {

    private static final Map<String, Map<String, Set<String>>> ENHANCEMENTS = new HashMap<>();

    private EnhancementManager() {
    }

    public static Map<String, Map<String, Set<String>>> getEnhancements() {
        return ENHANCEMENTS;
    }

    /**
     * Add interceptor information
     *
     * @param plugin plugin
     * @param interceptorList interceptor list
     * @param classLoader classLoader
     * @param methodDesc information of the enhanced method
     */
    public static void addEnhancements(Plugin plugin, List<Interceptor> interceptorList, ClassLoader classLoader,
                                       String methodDesc) {
        String enhancementKey = combinePluginInfo(plugin);
        if (methodDesc != null && !methodDesc.isEmpty()) {
            Map<String, Set<String>> methodDescMap = ENHANCEMENTS.computeIfAbsent(enhancementKey,
                    key -> new HashMap<>());
            String methodDescKey = combineEnhanceInfo(methodDesc, classLoader);
            Set<String> interceptorSet = methodDescMap.computeIfAbsent(methodDescKey, key -> new HashSet<>());
            for (Interceptor interceptor : interceptorList) {
                interceptorSet.add(interceptor.getClass().getCanonicalName());
            }
        }
    }

    /**
     * Clear the enhancement information of the plugin when uninstall it
     *
     * @param plugin plugin
     */
    public static void removePluginEnhancements(Plugin plugin) {
        ENHANCEMENTS.remove(combinePluginInfo(plugin));
    }

    /**
     * Clear cached enhancement information
     */
    public static void shutdown() {
        ENHANCEMENTS.clear();
    }

    /**
     * Combine plugin information
     */
    private static String combinePluginInfo(Plugin plugin) {
        return plugin.getName() + ":" + plugin.getVersion();
    }

    /**
     * Combine enhancement information
     */
    private static String combineEnhanceInfo(String methodDesc, ClassLoader classLoader) {
        return methodDesc + "@" + classLoader;
    }
}
