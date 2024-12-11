package com.bin.sm.router.matcher.pattern;





import com.bin.sm.router.RouterContext;
import com.bin.sm.util.StringUtil;

import java.util.Map;

/**
 * Matches with patterns like 'key=hello', 'key=hello*', 'key=*hello', 'key=h*o' or 'key=*'
 * <p>
 * This pattern evaluator must be the last one being executed.
 */
public class WildcardValuePattern implements ValuePattern {

    public static final WildcardValuePattern INSTANCE = new WildcardValuePattern();


    @Override
    public boolean shouldMatch(String pattern) {
        return pattern.indexOf("*") > 0;
    }

    @Override
    public boolean match(String pattern, String value) {
        return isMatchGlobPattern(pattern,value);
    }

    public static boolean isMatchGlobPattern(String pattern, String value, Map<String,Object> param) {
        if (param != null && pattern.startsWith("$")) {
            pattern = (String) param.get(pattern.substring(1));
        }
        return isMatchGlobPattern(pattern, value);
    }


    public static boolean isMatchGlobPattern(String pattern, String value) {
        if ("*".equals(pattern)) {
            return true;
        }
        if (StringUtil.isEmpty(pattern) && StringUtil.isEmpty(value)) {
            return true;
        }
        if (StringUtil.isEmpty(pattern) || StringUtil.isEmpty(value)) {
            return false;
        }

        int i = pattern.lastIndexOf('*');
        // doesn't find "*"
        if (i == -1) {
            return value.equals(pattern);
        }
        // "*" is at the end
        else if (i == pattern.length() - 1) {
            return value.startsWith(pattern.substring(0, i));
        }
        // "*" is at the beginning
        else if (i == 0) {
            return value.endsWith(pattern.substring(i + 1));
        }
        // "*" is in the middle
        else {
            String prefix = pattern.substring(0, i);
            String suffix = pattern.substring(i + 1);
            return value.startsWith(prefix) && value.endsWith(suffix);
        }
    }

}
