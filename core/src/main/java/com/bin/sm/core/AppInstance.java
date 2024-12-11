package com.bin.sm.core;

import com.bin.sm.circuitbreak.CircuitBreakerCluster;
import com.bin.sm.common.CriticalityType;
import com.bin.sm.context.Instance;
import com.bin.sm.context.NodeInstance;
import com.bin.sm.internal.collection.CopyOnWriteMap;
import com.bin.sm.ratelimiter.concurrency.Limiter;
import com.bin.sm.router.matcher.ConditionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * appName->AppInstance
 * 主动降级
 * 路由集合/负载集合
 * ip->node
 * 限流、熔断、降级
 * <p>
 * 监控
 * 优雅关机
 *
 *
 */
public class AppInstance extends AbstractInstance {
    private static Logger logger = LoggerFactory.getLogger(AppInstance.class);




    // key=path
    private final Map<String, GroupInstance> groups = new HashMap<>();
    // key=path
    private final Map<String, FuncInstance> funcs = new HashMap<>();

    private final Map<String, Set<String>> addressToTags = new HashMap<>();
    private final Map<String, Set<String>> tagToAddresses = new HashMap<>();



    // group -> func
    protected Map<String,Set<String>> groupMappingFunc = new CopyOnWriteMap<>();
    // func -> group
    protected Map<String,String> funcMappingGroup = new CopyOnWriteMap<>();


    protected Map<String, Limiter> groupLimiters = new CopyOnWriteMap<>();
    protected Map<String, Limiter> funcLimiters = new CopyOnWriteMap<>();

    protected Map<String, CircuitBreakerCluster> groupCircuitBreakerClusters = new CopyOnWriteMap<>();
    protected Map<String, CircuitBreakerCluster> funcCircuitBreakerClusters = new CopyOnWriteMap<>();

    protected Map<String, CriticalityType> resourceCriticalitys = new CopyOnWriteMap<>();

    // TODO
    protected LinkedList<ConditionRule> conditionRules = new LinkedList<>();;


    protected Map<String, Set<NodeInstance>> ipNodes = new CopyOnWriteMap<>();
    protected Map<String,NodeInstance> addressNodes = new CopyOnWriteMap<>();


    public Instance getInstance(String func) {
        FuncInstance funcInstance = funcs.get(func);
        if (funcInstance != null) {
            return funcInstance;
        }
        GroupInstance groupInstance = groups.get(func);
        if (groupInstance != null) {
            return groupInstance;
        }
        return this;
    }


    public Map<String,NodeInstance> getNodeInstanceByAddress() {
        return addressNodes;
    }

    public synchronized void createNodeInstance(NodeInstance nodeInstance) {
        String address = nodeInstance.getAddress();
        if (!getNodeInstanceByAddress().containsKey(address)) {
            Map<String, NodeInstance> newAddressNodes = new HashMap<>(addressNodes);
            Map<String, Set<NodeInstance>> newIpNodes = new HashMap<>(ipNodes);
            newAddressNodes.put(nodeInstance.getAddress(),nodeInstance);
            newIpNodes.getOrDefault(nodeInstance.getIp(),new HashSet<>()).add(nodeInstance);
            this.addressNodes = newAddressNodes;
            this.ipNodes = newIpNodes;
        }
    }

    public synchronized void expireNodes(List<String> expireAddresses) {
        Map<String, NodeInstance> newAddressNodes = new HashMap<>(addressNodes);
        Map<String, Set<NodeInstance>> newIpNodes = new HashMap<>(ipNodes);
        for (String expireAddress : expireAddresses) {
            NodeInstance remove = newAddressNodes.remove(expireAddress);
            if (remove != null) {
                Set<NodeInstance> nodeInstances = newIpNodes.get(remove.getIp());
                nodeInstances.remove(remove);
            }
        }
        this.addressNodes = newAddressNodes;
        this.ipNodes = newIpNodes;
    }

    public Map<String, Set<String>> getTagToAddresses() {
        return tagToAddresses;
    }

    public Map<String, Set<String>> getAddressToTags() {
        return addressToTags;
    }
}
