package com.bin.sm.router;

import com.bin.sm.context.NodeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class EnvRouter {

    private static Logger logger = LoggerFactory.getLogger(EnvRouter.class);

    public static final EnvRouter instance = new EnvRouter();

    private Set<String> ignoredEnvs = new HashSet<>();
    // 先环境->zone->tag
    public <N extends NodeAdapter> List<N> router(String localEnv, List<N> nodes) {
        if (nodes.size() <= 1) return nodes;
        // 那些环境 忽略
        if (ignoredEnvs.contains(localEnv)) {
            logger.debug("Env  feature is disabled! It could be enabled if the  property  to be set 'true'");
            return nodes;
        }

        List<N> sameEnvNodes = new LinkedList<>();

        for (N node : nodes) {
            String env = node.getEnv();

            if (Objects.equals(env, localEnv)) {
                sameEnvNodes.add(node);
            }
        }

        return sameEnvNodes;
    }
}
