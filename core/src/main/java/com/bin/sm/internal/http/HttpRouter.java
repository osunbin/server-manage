package com.bin.sm.internal.http;

import com.bin.sm.internal.http.protocol.HttpMethod;
import com.bin.sm.internal.http.protocol.HttpRequest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class HttpRouter {

    private static final List<HttpRouter> ROUTERS_MAPPING = new CopyOnWriteArrayList<>();


    public static Optional<HttpRouteHandler> getHandler(HttpRequest request) {
        List<HttpRouter> routers = ROUTERS_MAPPING;
        for (HttpRouter router : routers) {
            if (router.match(request)) {
                return Optional.of(router.getHandler());
            }
        }
        return Optional.empty();
    }

    public static void addHandler(HttpRouter handler) {
        ROUTERS_MAPPING.add(handler);
    }


    private final String path;

    private final HttpMethod method;

    private final HttpRouteHandler handler;

    public HttpRouter(String path, HttpMethod method, HttpRouteHandler handler) {
        this.path = path;
        this.method = method;
        this.handler = handler;
    }

    public boolean match(HttpRequest request) {
        if (!matchPath(request.getPath())) {
            return false;
        }
        if (HttpMethod.ALL.name().equals(method.name())) {
            return true;
        }
        if (method.name().equals(request.getMethod())) {
            return true;
        }
        return false;
    }

    private boolean matchPath(String uri) {
        if ("**".equals(path) || "/**".equals(path)) {
            return true;
        }
        if (path.equals(uri)) {
            return true;
        }
        return false;
    }

    public HttpRouteHandler getHandler() {
        return handler;
    }


}
