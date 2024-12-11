package com.bin.sm.plugin.agent.matcher;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;


public abstract class ClassFuzzyMatcher extends ClassMatcher {
    /**
     * Logical operation {@code not}, which returns true if false, false otherwise
     *
     * @return ClassFuzzyMatcher
     */
    public ClassFuzzyMatcher not() {
        final ClassFuzzyMatcher thisMatcher = this;
        return new ClassFuzzyMatcher() {
            @Override
            public boolean matches(TypeDescription typeDescription) {
                return !thisMatcher.matches(typeDescription);
            }
        };
    }

    /**
     * Logical operation {@code and} returns true if both is true, false otherwise
     *
     * @param matcher Another ClassFuzzyMatcher
     * @return ClassFuzzyMatcher
     */
    public ClassFuzzyMatcher and(ElementMatcher<TypeDescription> matcher) {
        final ClassFuzzyMatcher thisMatcher = this;
        return new ClassFuzzyMatcher() {
            @Override
            public boolean matches(TypeDescription typeDescription) {
                return thisMatcher.matches(typeDescription) && matcher.matches(typeDescription);
            }
        };
    }

    /**
     * Logical operation {@code or}, returns true if either is true, false otherwise
     *
     * @param matcher Another ClassFuzzyMatcher
     * @return ClassFuzzyMatcher
     */
    public ClassFuzzyMatcher or(ElementMatcher<TypeDescription> matcher) {
        final ClassFuzzyMatcher thisMatcher = this;
        return new ClassFuzzyMatcher() {
            @Override
            public boolean matches(TypeDescription typeDescription) {
                return thisMatcher.matches(typeDescription) || matcher.matches(typeDescription);
            }
        };
    }
}
