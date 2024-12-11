package com.bin.sm.router.matcher.pattern;





public interface ValuePattern {

    boolean shouldMatch(String pattern);


    boolean match(String pattern, String value);

}
