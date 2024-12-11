package com.bin.sm.context;

import com.bin.sm.core.InstanceType;
import com.bin.sm.router.matcher.ConditionRule;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
// Middleware
public interface Instance {

    String instanceName();

    InstanceType instanceType();

    Instance getParentInstance();

    boolean isDegrade();

    String fallback();

    Map<String,NodeInstance> getNodeInstanceByAddress();

    void expireNodes(long currentMillis);

    void expireNodes(List<String> expireAddresses);


    void createNodeInstance(NodeInstance nodeInstance);

    LinkedList<ConditionRule> getConditionRules();

    Map<String, Set<String>> getTagToAddresses();
}
