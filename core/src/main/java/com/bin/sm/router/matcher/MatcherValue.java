package com.bin.sm.router.matcher;

import java.util.List;

public class MatcherValue {
    // addresses、ip、tag
    private String key;
    private String operator;
    private List<ConditionResult> conditionResults;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<ConditionResult> getConditionResults() {
        return conditionResults;
    }

    public void setConditionResults(List<ConditionResult> conditionResults) {
        this.conditionResults = conditionResults;
    }


}
