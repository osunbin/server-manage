package com.bin.sm.extension.http;

import com.bin.sm.router.ConditionRouter;

import java.util.Collections;
import java.util.List;


public class HttpConditionRouter extends ConditionRouter<HttpRouterContext> {


    public static HttpConditionRouter instance = new HttpConditionRouter();



    public List<String> getKeyValue(HttpRouterContext routerContext, String source, String key) {
        switch (source) {
            case "cookie":
                return routerContext.getCookies(key);
            case "header":
            case "tag":
                return routerContext.getHeaders(key);
            case "path":
                return List.of(routerContext.getPath());
            case "address":
                return List.of(routerContext.getAddress());
            case "ip":
                return List.of(routerContext.getIp());
        }
        return Collections.emptyList();
    }


}
