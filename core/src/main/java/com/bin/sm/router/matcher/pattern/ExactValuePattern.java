package com.bin.sm.router.matcher.pattern;


public class ExactValuePattern implements ValuePattern{

    public static final ExactValuePattern INSTANCE = new ExactValuePattern();

    @Override
    public boolean shouldMatch(String pattern) {
        return true;
    }

    @Override
    public boolean match(String pattern, String value) {
        return pattern.equals(value);
    }
}
