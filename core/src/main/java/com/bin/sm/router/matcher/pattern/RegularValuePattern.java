package com.bin.sm.router.matcher.pattern;

import com.bin.sm.router.RouterContext;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegularValuePattern implements ValuePattern{

    public static final RegularValuePattern INSTANCE = new RegularValuePattern();

    @Override
    public boolean shouldMatch(String pattern) {
        try {
            Pattern.compile(pattern);
            return true;
        }catch (PatternSyntaxException e) {
            return false;
        }
    }

    @Override
    public boolean match(String pattern, String value) {
        return pattern.matches(value);
    }
}
