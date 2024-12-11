package com.bin.sm.core;

import com.bin.sm.common.CriticalityType;
import com.bin.sm.context.Instance;
import com.bin.sm.context.NodeInstance;
import com.bin.sm.router.matcher.ConditionRule;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractInstance implements Instance {
    private Instance parent;
    private String instanceName;
    // app group func
    private InstanceType instanceType;
    private CriticalityType criticality;
    protected LinkedList<ConditionRule> conditionRules = new LinkedList<>();;


    public String instanceName() {
        return instanceName;
    }

    public InstanceType instanceType(){
        return instanceType;
    }


    public boolean isDegrade() {
        return false;
    }

    public String fallback(){
        return "respone";
    }


    public Map<String, NodeInstance> getNodeInstanceByAddress() {
        return findParent(Instance::getNodeInstanceByAddress);
    }

    public Instance getParentInstance() {
        return parent;
    }

    public LinkedList<ConditionRule> getConditionRules() {
        LinkedList<ConditionRule> newConditionRules = new LinkedList<>(conditionRules);
        if (parent != null) {
            newConditionRules.addAll(parent.getConditionRules());
        }
        return newConditionRules;
    }

    public Map<String, Set<String>> getTagToAddresses() {
        return findParent(Instance::getTagToAddresses);
    }

    public synchronized void createNodeInstance(NodeInstance nodeInstance) {
        findParent((app) -> {
            app.createNodeInstance(nodeInstance);
        });
    }

    public void expireNodes(long currentMillis) {
        List<String> expireAddress = getNodeInstanceByAddress().values().stream().filter(node -> {
            long lastUsed = node.getLastUsed();
            return currentMillis - lastUsed > Governance.INSTANCE.getNodeExpireTime();
        }).map(NodeInstance::getAddress).toList();
        expireNodes(expireAddress);
    }

    public void expireNodes(List<String> expireAddresses) {
        findParent((app) -> {
            app.expireNodes(expireAddresses);
        });
    }

    private <T> T findParent(Function<AppInstance, T> func) {
        Instance curInstance = parent;
        while (curInstance != null) {
            if (curInstance instanceof AppInstance) {
                return func.apply((AppInstance) curInstance);
            } else {
                curInstance = curInstance.getParentInstance();
            }
        }
        return null;
    }

    private void findParent(Consumer<AppInstance> consumer) {
        Instance curInstance = parent;
        while (curInstance != null) {
            if (curInstance instanceof AppInstance) {
                consumer.accept((AppInstance)curInstance);
            } else {
                curInstance = curInstance.getParentInstance();
            }
        }
    }

    // 升序 Priority 越小,越先执行
    protected synchronized void changeConditionRule(ConditionRule conditionRule) {
        LinkedList<ConditionRule> newConditionRules = new LinkedList<>(conditionRules);
        newConditionRules.add(conditionRule);
        newConditionRules.sort((o1, o2) -> {
            int priority1 = o1.getPriority();
            int priority2 = o2.getPriority();
            return priority1 - priority2;
        });
        this.conditionRules = newConditionRules;
    }
}
