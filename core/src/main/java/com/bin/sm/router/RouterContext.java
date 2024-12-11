package com.bin.sm.router;

import com.bin.sm.router.matcher.ConditionRule;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  路由key
 *  priority 优先级
 *  force 强制执行(路由结果为空时)
 *  isRunning  开启/关闭
 *  when 条件
 *  then 结果
 *
 */
public class RouterContext {

    protected Map<String, Set<String>> tagToAddresses = new HashMap<>();

    private LinkedList<ConditionRule> conditionRules = new LinkedList<>();
    private String rpcType;
    private String func;
    private String localEnv;
    private String localZone;

    public RouterContext(String rpcType) {
        this.rpcType = rpcType;
    }


    public RouterContext(String rpcType,String func) {
        this.rpcType = rpcType;
        this.func = func;
    }

    public String localEnv() {
        return localEnv;
    }

    public String localZone() {
        return localZone;
    }

    public List<String> getTag() {
       return Collections.emptyList();
    }

    public Set<String> getAddressesByTag(String tag) {
        return tagToAddresses.get(tag);
    }

    public LinkedList<ConditionRule> getConditionRules() {
        return conditionRules;
    }


    public String getFunc() {
        return func;
    }

    public String getRpcType() {
        return rpcType;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public void setLocalZone(String localZone) {
        this.localZone = localZone;
    }

    public void setLocalEnv(String localEnv) {
        this.localEnv = localEnv;
    }

    public void setConditionRules(LinkedList<ConditionRule> conditionRules) {
        this.conditionRules = conditionRules;
    }

    public void setTagToAddresses(Map<String, Set<String>> tagToAddresses) {
        this.tagToAddresses = tagToAddresses;
    }
}
