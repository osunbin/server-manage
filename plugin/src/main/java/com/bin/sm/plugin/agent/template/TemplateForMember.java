package com.bin.sm.plugin.agent.template;

import com.bin.sm.plugin.agent.ExecuteContext;
import com.bin.sm.plugin.agent.adviser.AdviserScheduler;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Method;

public class TemplateForMember {
    private TemplateForMember() {
    }

    /**
     * The preceding trigger point of method
     *
     * @param cls enhanced class
     * @param obj the object being enhanced
     * @param method the method being enhanced
     * @param methodKey method key, which is used to find template class
     * @param arguments arguments of method
     * @param adviceKey advice class name
     * @param context execute context
     * @param isSkip Whether to skip the main execution of method
     * @return Skip result
     * @throws Throwable execute exception
     *
     */
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean onMethodEnter(@Advice.Origin Class<?> cls,
            @Advice.This(typing = Assigner.Typing.DYNAMIC) Object obj,
            @Advice.Origin Method method, @Advice.Origin("#t\\##m#s") String methodKey,
            @Advice.AllArguments(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object[] arguments,
            @Advice.Local(value = "_ADVICE_KEY_$SERMANT_LOCAL") String adviceKey,
            @Advice.Local(value = "_EXECUTE_CONTEXT_$SERMANT_LOCAL") Object context,
            @Advice.Local(value = "_IS_SKIP_$SERMANT_LOCAL") Boolean isSkip

    ) throws Throwable {
        adviceKey = "TemplateForMember_" + Integer.toHexString(methodKey.hashCode()) + "_" + cls.getClassLoader();
        context = ExecuteContext.forMemberMethod(obj, method, arguments, null, null);
        context = AdviserScheduler.onMethodEnter(context, adviceKey);
        arguments = ((ExecuteContext) context).getArguments();
        isSkip = ((ExecuteContext) context).isSkip();
        return isSkip;
    }

    /**
     * The post trigger point of method
     *
     * @param result Method execution result
     * @param throwable Method execution exception
     * @param adviceKey advice class name
     * @param context execute context
     * @param isSkip Whether to skip the main execution of method
     * @throws Throwable execute exception
     */
    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onMethodExit(@Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object result,
            @Advice.Thrown(readOnly = false) Throwable throwable,
            @Advice.Local(value = "_ADVICE_KEY_$SERMANT_LOCAL") String adviceKey,
            @Advice.Local(value = "_EXECUTE_CONTEXT_$SERMANT_LOCAL") Object context,
            @Advice.Local(value = "_IS_SKIP_$SERMANT_LOCAL") Boolean isSkip) throws Throwable {
        context = isSkip ? context : ((ExecuteContext) context).afterMethod(result, throwable);
        context = AdviserScheduler.onMethodExit(context, adviceKey);
        result = ((ExecuteContext) context).getResult();
        if (((ExecuteContext) context).isChangeThrowable()) {
            throwable = ((ExecuteContext) context).getThrowable();
        }
    }
}