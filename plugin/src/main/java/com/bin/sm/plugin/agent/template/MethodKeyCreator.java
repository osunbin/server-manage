package com.bin.sm.plugin.agent.template;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class MethodKeyCreator {
    private MethodKeyCreator() {
    }

    /**
     * Builds the method key of the constructor
     *
     * @param constructor constructor
     * @return method key
     */
    public static String getConstKey(Constructor<?> constructor) {
        final StringBuilder sb = new StringBuilder().append(constructor.getName()).append("#<init>(");
        final Class<?>[] parameters = constructor.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(parameters[i].getName());
        }
        sb.append(')');
        return sb.toString();
    }

    /**
     * Build a method key for a method
     *
     * @param method method
     * @return method key
     */
    public static String getMethodKey(Method method) {
        if (method == null) {
            return "#<init>()";
        }
        final StringBuilder sb = new StringBuilder()
                .append(method.getDeclaringClass().getName())
                .append('#')
                .append(method.getName())
                .append('(');
        final Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(parameters[i].getName());
        }
        sb.append(')');
        return sb.toString();
    }

    /**
     * Build a method key for a MethodDescription
     *
     * @param methodDesc method description
     * @return method key
     */
    public static String getMethodDescKey(MethodDescription.InDefinedShape methodDesc) {
        final StringBuilder sb = new StringBuilder().append(methodDesc.getDeclaringType().asErasure().getTypeName());
        if (methodDesc.isConstructor()) {
            sb.append("#<init>(");
        } else {
            sb.append('#').append(methodDesc.getActualName()).append("(");
        }
        final ParameterList<ParameterDescription.InDefinedShape> parameters = methodDesc.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(parameters.get(i).getType().asErasure().getTypeName());
        }
        sb.append(')');
        return sb.toString();
    }
}
