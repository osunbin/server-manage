package com.bin.sm.router.matcher;

import java.util.StringJoiner;

public class ConditionResult {
   private   String value;
   private   int weight;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ConditionResult.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .add("weight=" + weight)
                .toString();
    }
}
