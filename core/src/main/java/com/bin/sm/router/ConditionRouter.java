package com.bin.sm.router;



import com.bin.sm.core.Governance;
import com.bin.sm.context.NodeAdapter;
import com.bin.sm.core.GovernanceContext;
import com.bin.sm.router.matcher.Condition;
import com.bin.sm.router.matcher.ConditionResult;
import com.bin.sm.router.matcher.ConditionRule;
import com.bin.sm.router.matcher.ConditionValues;
import com.bin.sm.router.matcher.MatcherValue;
import com.bin.sm.router.matcher.pattern.ValuePattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class ConditionRouter<R extends RouterContext> implements Router {
    protected final Logger logger = LogManager.getLogger(ConditionRouter.class);




    // (1 && 2) || 3
    // 条件
    // 数据来源  数据key  条件(=,!=)  值(xx,xx)
    //
    // 结果
    //   addresses =  xxx
    //           tag =  xxxx(25),xxx(100)
    // ConditionRule
    public <N extends NodeAdapter> List<N> router(R routerContext, List<N> nodes, ConditionRule conditionRule) {


        boolean enable = conditionRule.isEnable();
        if (!enable) {
            return nodes;
        }

        if (!condition(conditionRule, routerContext)) {
            return nodes;
        }

        List<N> newNodes = new ArrayList<>(nodes);
        Iterator<N> iterator = newNodes.iterator();
        while (iterator.hasNext()) {
            N nodeAdapter = iterator.next();
            boolean matched = matchThen(routerContext, nodeAdapter, conditionRule.getMatcherValues());
            if (!matched) {
                iterator.remove();
            }
        }


        boolean force = conditionRule.isForce();
        if (newNodes.isEmpty()) {
            // 没有节点时,非强制执行,还原
            if (!force) return nodes;
        }
        return newNodes;
    }

    protected boolean condition(ConditionRule conditionRule, R routerContext) {

        Map<String, Object> vars = new HashMap<>();


        List<Condition> conditions = conditionRule.getConditions();
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        for (Condition condition : conditions) {
            String source = condition.getSource();
            String key = condition.getKey();

            List<String> keyValues = getKeyValue(routerContext, source, key);

            boolean match = isMatch(condition, keyValues);

            if (conditions.size() == 1) {
                return match;
            }
            vars.put(condition.getMark(), match);
        }

        return conditionRule.executeExp(vars);
    }

    private  boolean isMatch(Condition condition, List<String> keyValues) {
        boolean match = false;
        List<ConditionValues> conditionValues = condition.getConditionValues();
        for (ConditionValues conditionValue : conditionValues) {
            ValuePattern valuePattern = conditionValue.getValuePattern();
            String pattern = conditionValue.getValue();

            for (String keyValue : keyValues) {
                if (valuePattern.match(pattern, keyValue)) {
                    match = true;
                    break;
                }
            }
        }

        String operator = condition.getOperator();
        if ("!=".equals(operator)) {
            match = !match;
        }
        return match;
    }


    protected <N extends NodeAdapter> boolean matchThen(R routerContext, N nodeAdapter, List<MatcherValue> matcherValues) {
        boolean matchAll = true;

        for (MatcherValue matcherValue : matcherValues) {
            String key = matcherValue.getKey();
            boolean match = true;
            String operator = matcherValue.getOperator();
            if ("!=".equals(operator)) {
                match = false;
            }
            List<ConditionResult> conditionResults = matcherValue.getConditionResults();
            for (ConditionResult conditionResult : conditionResults) {
                String value = conditionResult.getValue();
                int weight = conditionResult.getWeight();
                switch (key) {
                    case "tag":

                        String addressStr = nodeAdapter.getAddressStr();
                        Set<String> addresses = routerContext.getAddressesByTag(value);
                        if (addresses != null && !addresses.isEmpty()) {
                            if (addresses.contains(addressStr)) {
                                if (weight > 0)  {
                                    nodeAdapter.setWeight(weight);
                                    GovernanceContext.setRandomWeight();
                                }
                                return match;
                            }
                        } else {
                            if (nodeAdapter.existTag(value)) {
                                if (weight > 0) {
                                    nodeAdapter.setWeight(weight);
                                    GovernanceContext.setRandomWeight();
                                }
                                return match;
                            }
                        }

                        break;
                    case "ip":
                        String ip = nodeAdapter.getIp();
                        if (ip.equals(value)) {
                            if (weight > 0) {
                                nodeAdapter.setWeight(weight);
                                GovernanceContext.setRandomWeight();
                            }
                            return match;
                        }
                        break;
                    case "address":
                        if (value.equals(nodeAdapter.getAddressStr())) {
                            if (weight > 0) {
                                nodeAdapter.setWeight(weight);
                                GovernanceContext.setRandomWeight();
                            }
                            return match;
                        }
                        break;
                }
            }
            matchAll &= !match;
        }
        return matchAll;
    }

    protected List<String> getKeyValue(R routerContext, String source, String key) {
        return Collections.emptyList();
    }


}
