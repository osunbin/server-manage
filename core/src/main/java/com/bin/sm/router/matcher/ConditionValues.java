package com.bin.sm.router.matcher;

import com.bin.sm.router.matcher.pattern.ValuePattern;

public class ConditionValues {
    private String value;
    private ValuePattern valuePattern;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ValuePattern getValuePattern() {
        return valuePattern;
    }

    public void setValuePattern(ValuePattern valuePattern) {
        this.valuePattern = valuePattern;
    }
}
