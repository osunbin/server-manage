package com.bin.sm.plugin.agent.template;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;

public class ImplDelegator {

    /**
     * Interface implementation instance
     */
    private final Object implInstance;

    /**
     * Constructor
     *
     * @param implInstance Interface implementation instance
     */
    public ImplDelegator(Object implInstance) {
        this.implInstance = implInstance;
    }

    /**
     * A delegate method used to implement an interface, a method that proxies a method call to an interface
     * implementation instance
     *
     * @param rawObject raw object
     * @param rawMethod raw method
     * @param args args
     * @return result
     * @throws Exception Exception
     */
    @RuntimeType
    public Object impl(@This Object rawObject, @Origin Method rawMethod, @AllArguments Object[] args) throws Exception {
        if (implInstance instanceof ImplTemplate) {
            ((ImplTemplate) implInstance).setRawObject(rawObject);
        }
        return implInstance.getClass().getMethod(rawMethod.getName(), rawMethod.getParameterTypes())
                .invoke(implInstance, args);
    }

    /**
     * Template class for interface implementation. Inheriting an implementation instance of this class will result
     * in an enhanced raw object
     *
     * @since 2022-01-24
     */
    public static class ImplTemplate {
        /**
         * raw object
         */
        protected Object rawObject;

        /**
         * set raw object
         *
         * @param rawObject raw object
         */
        public void setRawObject(Object rawObject) {
            this.rawObject = rawObject;
        }
    }
}
