package com.bin.sm.plugin.agent.template;


import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.adviser.AdviserScheduler;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Constructor;

/**
 *  构造函数
 */
public class TemplateForCtor {
    private TemplateForCtor() {
    }

    /**
     * The preceding trigger point of method
     *
     * @param cls enhanced class
     * @param constructor constructor
     * @param methodKey method key, which is used to find template class
     * @param arguments arguments of method
     * @param adviceKey advice class name
     * @param context execute context
     * @throws Throwable execute exception
     */
    @Advice.OnMethodEnter
    public static void onMethodEnter(
            @Advice.Origin Class<?> cls,
            @Advice.Origin Constructor<?> constructor,
            @Advice.Origin("#t\\##m#s") String methodKey,
            @Advice.AllArguments(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object[] arguments,
            @Advice.Local(value = "_ADVICE_KEY_$SERMANT_LOCAL") String adviceKey,
            @Advice.Local(value = "_EXECUTE_CONTEXT_$SERMANT_LOCAL") Object context
    ) throws Throwable {
        adviceKey = "TemplateForCtor_" + Integer.toHexString(methodKey.hashCode()) + "_" + cls.getClassLoader();
        context = ExecuteContext.forConstructor(cls, constructor, arguments, null);
        context = AdviserScheduler.onMethodEnter(context, adviceKey);
        arguments = ((ExecuteContext) context).getArguments();
    }

    /**
     * The post trigger point of method
     *
     * @param obj the object being enhanced
     * @param adviceKey advice class name
     * @param context execute context
     * @throws Throwable execute exception
     */
    @Advice.OnMethodExit
    public static void onMethodExit(
            @Advice.This(typing = Assigner.Typing.DYNAMIC) Object obj,
            @Advice.Local(value = "_ADVICE_KEY_$SERMANT_LOCAL") String adviceKey,
            @Advice.Local(value = "_EXECUTE_CONTEXT_$SERMANT_LOCAL") Object context
    ) throws Throwable {
        context = ((ExecuteContext) context).afterConstructor(obj, null);
        AdviserScheduler.onMethodExit(context, adviceKey);
    }
}
