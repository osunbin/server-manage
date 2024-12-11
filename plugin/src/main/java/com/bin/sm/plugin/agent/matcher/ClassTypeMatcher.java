package com.bin.sm.plugin.agent.matcher;

import java.util.Set;

public abstract class ClassTypeMatcher extends ClassMatcher {
    /**
     * get type name set
     *
     * @return type name set
     */
    public abstract Set<String> getTypeNames();
}
