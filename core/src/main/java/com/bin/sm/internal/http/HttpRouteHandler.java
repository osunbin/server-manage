package com.bin.sm.internal.http;

import com.bin.sm.internal.http.protocol.HttpRequest;
import com.bin.sm.internal.http.protocol.HttpResponse;

public interface HttpRouteHandler {
    /**
     * Processes an HTTP request
     *
     * @param request HTTP request object
     * @param response HTTP response object
     * @throws Exception if an exception occurs during processing
     */
    void handle(HttpRequest request, HttpResponse response) throws Exception;
}
