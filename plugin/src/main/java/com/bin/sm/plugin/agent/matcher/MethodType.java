package com.bin.sm.plugin.agent.matcher;

import net.bytebuddy.description.method.MethodDescription;


public enum MethodType {
    /**
     * static method
     */
    STATIC() {
        @Override
        public boolean match(MethodDescription methodDescription) {
            return methodDescription.isStatic();
        }
    },
    /**
     * constructor
     */
    CONSTRUCTOR() {
        @Override
        public boolean match(MethodDescription methodDescription) {
            return methodDescription.isConstructor();
        }
    },
    /**
     * member method
     */
    MEMBER() {
        @Override
        public boolean match(MethodDescription methodDescription) {
            return !methodDescription.isStatic() && !methodDescription.isConstructor();
        }
    },
    /**
     * public method
     */
    PUBLIC() {
        @Override
        public boolean match(MethodDescription methodDescription) {
            return methodDescription.isPublic();
        }
    };

    /**
     * Check whether the method description matches
     *
     * @param methodDescription method description
     * @return match result
     */
    public abstract boolean match(MethodDescription methodDescription);
}
