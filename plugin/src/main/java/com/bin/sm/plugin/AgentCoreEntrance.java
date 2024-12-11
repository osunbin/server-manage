package com.bin.sm.plugin;

import com.bin.sm.plugin.agent.ByteEnhanceManager;
import com.bin.sm.plugin.agent.adviser.AdviserInterface;
import com.bin.sm.plugin.agent.adviser.AdviserScheduler;
import com.bin.sm.plugin.agent.template.DefaultAdviser;
import com.bin.sm.plugin.common.BootArgsIndexer;

import java.lang.instrument.Instrumentation;
import java.util.Map;

public class AgentCoreEntrance {

    private static String artifactCache;
    private static AdviserInterface adviserCache;

    public static void install(String artifact, Map<String, Object> argsMap, Instrumentation instrumentation,
                               boolean isDynamic) throws Exception {


        artifactCache = artifact;
        adviserCache = new DefaultAdviser();

        BootArgsIndexer.build(argsMap);

        ByteEnhanceManager.init(instrumentation);


        AdviserScheduler.registry(adviserCache);

        // After all static plugins are loaded, they are enhanced in a unified manner, using one AgentBuilder
        if (!isDynamic) {
            ByteEnhanceManager.enhance();
        }

    }
}
