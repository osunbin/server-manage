package com.bin.sm.router;

import com.bin.sm.context.NodeAdapter;
import com.bin.sm.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ZoneRouter {

    private static Logger logger = LoggerFactory.getLogger(ZoneRouter.class);

    public static final ZoneRouter instance = new ZoneRouter();

    private Set<String> disabledZones = new HashSet<>();
    private boolean zoneEnabled = true;
    private int upstreamReadyPercentage = 60; // 60%
    private int sameZoneMinAvailable = 2;


    public <N extends NodeAdapter> List<N> router(String localZone, List<N> nodes) {

        if (nodes.size() <= 1) return nodes;
        if (!zoneEnabled) {
            logger.debug("Zone  feature is disabled! It could be enabled if the  property  to be set 'true'");
            return nodes;
        }

        if (isIgnored(localZone)) {
            logger.debug("Zone Preference feature will be ignored , caused by zone : '{}'", localZone);
            return nodes;
        }

        int totalSize = nodes.size();
        List<N> targetInstances = nodes;
        // 黑名单
        if (!disabledZones.isEmpty()) {
            targetInstances = filterDisabledZone(nodes);
            if (targetInstances.size() <= 1) {
                logger.debug("Not enough node available after disabled zone['{}'] filter, " + "the nodes' total size : {} -> actual size : {}",
                        disabledZones, totalSize, targetInstances.size());
                return targetInstances;
            }
            totalSize = targetInstances.size();
        }


        List<N> sameZoneNodes = new LinkedList<>();

        for (N node : targetInstances) {
            String zone = node.getZone();

            if (Objects.equals(zone, localZone)) {
                sameZoneNodes.add(node);
            }

        }


        if (isUpstreamZoneNotReady(sameZoneNodes.size(), totalSize, upstreamReadyPercentage)) {
            logger.debug("The ready percentage of nodes with zone is under the threshold [{}%], total nodes size : {} , "
                    + "ready nodes size : {}", upstreamReadyPercentage, totalSize, sameZoneNodes.size());
            return targetInstances;
        }

        int sameZoneNodesSize = sameZoneNodes.size();
        if (sameZoneNodesSize > 0) {
            if (sameZoneNodesSize < sameZoneMinAvailable) {
                logger.debug("The size of same zone ['{}'] nodes is under the threshold : {}, actual size : {}", localZone, sameZoneMinAvailable,
                        sameZoneNodesSize);
                return targetInstances; // 最小可用
            }
            logger.debug("The same zone ['{}'] nodes[size : {} , total : {}] are found!", localZone, sameZoneNodesSize, totalSize);
            return sameZoneNodes;
        }
        // No matched
        logger.debug("No same zone ['{}'] node was found, total nodes size : {} ", localZone, totalSize);
        return targetInstances;
    }

    private boolean isIgnored(String zone) {
        return StringUtil.isBlank(zone) || "defaultZone".equalsIgnoreCase(zone);
    }

    private <N extends NodeAdapter> List<N> filterDisabledZone(List<N> nodes) {
        List<N> targetNodes = new LinkedList<>();
        for (N node : nodes) {
            String zone = node.getZone();
            if (!disabledZones.contains(zone)) {
                targetNodes.add(node);
            }
        }
        return targetNodes;
    }

    private boolean isUpstreamZoneNotReady(int zoneCount, int nodeSize, int zonePercentThreshold) {
        int percent = (zoneCount * 100 / nodeSize);
        return percent < zonePercentThreshold;
    }
}
