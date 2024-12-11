package com.bin.sm.router;


public class ConditionRouterFactory {


    public static final ConditionRouterFactory INSTANCE = new ConditionRouterFactory();


    public <R extends RouterContext> ConditionRouter<R> getConditionRouter(String type) {
        if ("http".equals(type)) {
            return (ConditionRouter<R>) HttpConditionRouter.instance;
        }
        throw new IllegalArgumentException("type:"+type+" is invalid");
    }
}
