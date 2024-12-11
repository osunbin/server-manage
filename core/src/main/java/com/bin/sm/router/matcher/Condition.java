package com.bin.sm.router.matcher;

import java.util.List;

public class Condition {
    // a,b,c
    private String mark;
    // 数据来源  cookie header  path  ip tag
    private String source;
    // gray
    private String key;
    // 运算符
    private String operator;
    // xx,xx
    private List<ConditionValues> conditionValues;


    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }



    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

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


    public List<ConditionValues> getConditionValues() {
        return conditionValues;
    }

    public void setConditionValues(List<ConditionValues> conditionValues) {
        this.conditionValues = conditionValues;
    }
}
