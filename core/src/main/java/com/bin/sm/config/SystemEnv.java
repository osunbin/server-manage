package com.bin.sm.config;

import com.bin.sm.util.StringUtil;

public class SystemEnv {

    public static String get(String name) {
        String result = System.getenv(name);
        if (StringUtil.isNotEmpty(result))
            return result;
        result = System.getProperty(name);
        if (StringUtil.isNotEmpty(result))
            return result;
        return null;
    }
}
