package com.bin.sm.plugin.agent;

import com.bin.sm.plugin.Plugin;
import com.bin.sm.plugin.agent.config.AgentConfig;
import com.bin.sm.plugin.agent.declarer.PluginDescription;
import com.bin.sm.plugin.utils.FileUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.BatchAllocator.ForTotal;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.DiscoveryStrategy.Reiterating;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import java.lang.instrument.Instrumentation;
import java.util.List;

public class ByteEnhanceManager {
    private static Instrumentation instrumentationCache;

    private static BufferedAgentBuilder builder;

    private ByteEnhanceManager() {
    }

    /**
     * Initialization
     *
     * @param instrumentation instrumentation
     */
    public static void init(Instrumentation instrumentation) {
        instrumentationCache = instrumentation;
        builder = BufferedAgentBuilder.build();

        // Once initialization is complete, an Action is added to add bytecode enhancements introduced directly by
        // the framework
        enhanceForFramework();
    }

    /**
     * Install classloader enhanced bytecode for premain only
     */
    public static void enhance() {
        cacheUnmatchedClass();
        builder.install(instrumentationCache);
        saveUnMatchedClass();
    }

    private static void saveUnMatchedClass() {
        if (AgentConfig.INSTANCE.isPreFilterEnable()) {
            Runtime.getRuntime().addShutdownHook(new Thread(FileUtils::writeUnmatchedClassNameToFile
            ));
        }
    }

    private static void cacheUnmatchedClass() {
        FileUtils.readUnmatchedClassNameFromFile();
    }

    /**
     * Bytecode enhancement based on plugins that support static installation
     *
     * @param plugin plugin that supports static installation
     */
    public static void enhanceStaticPlugin(Plugin plugin) {
        if (plugin.isDynamic()) {
            return;
        }
        builder.addPlugins(PluginCollector.getDescriptions(plugin));
    }

    /**
     * Bytecode enhancement based on plugins that support dynamic installation
     *
     * @param plugin plugin that supports dynamic installation
     */
    public static void enhanceDynamicPlugin(Plugin plugin) {
        if (!plugin.isDynamic()) {
            return;
        }
        List<PluginDescription> plugins = PluginCollector.getDescriptions(plugin);
        ResettableClassFileTransformer resettableClassFileTransformer = BufferedAgentBuilder.build()
                .addPlugins(plugins).install(instrumentationCache);
        plugin.setClassFileTransformer(resettableClassFileTransformer);
    }

    /**
     * Uninstall bytecode enhancements for plugins that support dynamic installation
     *
     * @param plugin plugin that supports dynamic installation
     */
    public static void unEnhanceDynamicPlugin(Plugin plugin) {
        if (!plugin.isDynamic()) {
            return;
        }
        plugin.getClassFileTransformer().reset(instrumentationCache, RedefinitionStrategy.RETRANSFORMATION,
                Reiterating.INSTANCE, ForTotal.INSTANCE,
                AgentBuilder.RedefinitionStrategy.Listener.StreamWriting.toSystemOut());
    }

    private static void enhanceForFramework() {
        enhanceForInjectService();
    }

    /**
     * An enhancement to the classloader was introduced to help inject classes work with Sermant classes
     */
    private static void enhanceForInjectService() {
//        if (ConfigManager.getConfig(ServiceConfig.class).isInjectEnable()) {
//            builder.addEnhance(new ClassLoaderDeclarer());
//        }
    }
}
