//package com.bin.sm.springboot;
//
//
//import feign.Feign;
//import feign.InvocationHandlerFactory;
//import feign.MethodMetadata;
//import feign.Target;
//import feign.Util;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import org.springframework.cloud.openfeign.FallbackFactory;
//
//public class SentinelInvocationHandler implements InvocationHandler {
//    private final Target<?> target;
//    private final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;
//    private FallbackFactory fallbackFactory;
//    private Map<Method, Method> fallbackMethodMap;
//
//    SentinelInvocationHandler(Target<?> target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch, FallbackFactory fallbackFactory) {
//        this.target = (Target) Util.checkNotNull(target, "target", new Object[0]);
//        this.dispatch = (Map) Util.checkNotNull(dispatch, "dispatch", new Object[0]);
//        this.fallbackFactory = fallbackFactory;
//        this.fallbackMethodMap = toFallbackMethod(dispatch);
//    }
//
//    SentinelInvocationHandler(Target<?> target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
//        this.target = (Target) Util.checkNotNull(target, "target", new Object[0]);
//        this.dispatch = (Map) Util.checkNotNull(dispatch, "dispatch", new Object[0]);
//    }
//
//    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
//        if ("equals".equals(method.getName())) {
//            try {
//                Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
//                return this.equals(otherHandler);
//            } catch (IllegalArgumentException var21) {
//                return false;
//            }
//        } else if ("hashCode".equals(method.getName())) {
//            return this.hashCode();
//        } else if ("toString".equals(method.getName())) {
//            return this.toString();
//        } else {
//            InvocationHandlerFactory.MethodHandler methodHandler = (InvocationHandlerFactory.MethodHandler) this.dispatch.get(method);
//            Target var7 = this.target;
//            Object result;
//            if (!(var7 instanceof Target.HardCodedTarget)) {
//                result = methodHandler.invoke(args);
//            } else {
//                Target.HardCodedTarget hardCodedTarget = (Target.HardCodedTarget) var7;
//                Map var10000 = SentinelContractHolder.METADATA_MAP;
//                String var10001 = hardCodedTarget.type().getName();
//                MethodMetadata methodMetadata = (MethodMetadata) var10000.get(var10001 + Feign.configKey(hardCodedTarget.type(), method));
//                if (methodMetadata == null) {
//                    result = methodHandler.invoke(args);
//                } else {
//                    // method+url+path
//                    String var28 = methodMetadata.template().method().toUpperCase();
//                    String resourceName = var28 + ":" + hardCodedTarget.url() + methodMetadata.template().path();
//
//
//                    Object var12;
//
//                    Throwable ex;
//                    try {
//                        result = methodHandler.invoke(args);
//                        return result;
//                    } catch (Throwable var22) {
//                        ex = var22;
//                    }
//
//                    if (this.fallbackFactory == null) {
//                        throw ex;
//                    }
//
//                    try {
//                        Object fallbackResult = ((Method) this.fallbackMethodMap.get(method)).invoke(this.fallbackFactory.create(ex), args);
//                        var12 = fallbackResult;
//                    } catch (IllegalAccessException var19) {
//                        IllegalAccessException e = var19;
//                        throw new AssertionError(e);
//                    } catch (InvocationTargetException var20) {
//                        InvocationTargetException e = var20;
//                        throw e.getCause();
//                    }
//
//                    return var12;
//                }
//            }
//
//            return result;
//        }
//    }
//
//    public boolean equals(Object obj) {
//
//        if (obj instanceof SentinelInvocationHandler sentinelInvocationHandler) {
//            return this.target.equals(sentinelInvocationHandler.target);
//        } else {
//            return false;
//        }
//    }
//
//    public int hashCode() {
//        return this.target.hashCode();
//    }
//
//    public String toString() {
//        return this.target.toString();
//    }
//
//    static Map<Method, Method> toFallbackMethod(Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
//        Map<Method, Method> result = new LinkedHashMap();
//        Iterator var2 = dispatch.keySet().iterator();
//
//        while (var2.hasNext()) {
//            Method method = (Method) var2.next();
//            method.setAccessible(true);
//            result.put(method, method);
//        }
//
//        return result;
//    }
//}
