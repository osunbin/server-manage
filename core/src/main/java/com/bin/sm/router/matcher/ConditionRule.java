package com.bin.sm.router.matcher;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionRule {
    private String routerKey;
    // feign   dubbo
    private String type;
    private boolean enable;
    private int priority;
    // 强制执行(路由结果为空时)
    private boolean force;
    // 条件
    private List<Condition> conditions;
    // (a&&b)||c
    private String execute;

    private Expression compiledExp;

    // 结果
    private List<MatcherValue> matcherValues;


    public String getRouterKey() {
        return routerKey;
    }

    public void setRouterKey(String routerKey) {
        this.routerKey = routerKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public String getExecute() {
        return execute;
    }

    public void setExecute(String execute) {
        if (execute != null) {
            this.compiledExp = AviatorEvaluator.compile(execute);
        }
        this.execute = execute;
    }

    public boolean executeExp(Map<String,Object> vars) {
        return (boolean) compiledExp.execute(vars);
    }

    public List<MatcherValue> getMatcherValues() {
        return matcherValues;
    }

    public void setMatcherValues(List<MatcherValue> matcherValues) {
        this.matcherValues = matcherValues;
    }
}
