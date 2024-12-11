package com.bin.sm.internal.http.protocol;

import com.bin.sm.internal.http.HttpServerException;
import com.bin.sm.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class SimpleHttpResponse implements HttpResponse {
    private final HttpExchange exchange;

    private int status = 200;

    /**
     * Constructs a SimpleHttpResponse object.
     *
     * @param exchange The HttpExchange object for server communication
     */
    public SimpleHttpResponse(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public HttpResponse setStatus(int code) {
        this.status = code;
        return this;
    }

    @Override
    public HttpResponse addHeader(String name, String value) {
        exchange.getResponseHeaders().add(name, value);
        return this;
    }

    @Override
    public HttpResponse setHeader(String name, String value) {
        exchange.getResponseHeaders().set(name, value);
        return this;
    }

    @Override
    public HttpResponse setHeaders(Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            setHeader(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public HttpResponse setContentType(String contentType) {
        if (!contentType.contains(";")) {
            setHeader("Content-Type", contentType + ";charset=" + StandardCharsets.UTF_8);
            return this;
        }
        setHeader("Content-Type", contentType);
        return this;
    }

    @Override
    public HttpResponse setContentLength(long size) {
        setHeader("Content-Length", String.valueOf(size));
        return this;
    }

    @Override
    public void writeBody(Throwable ex) {
        this.writeBody(ex.getMessage());
    }

    @Override
    public void writeBody(byte[] bytes) {
        try (OutputStream out = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(status, bytes.length);
            out.write(bytes);
        } catch (Exception ex) {
            throw new HttpServerException(500, ex);
        }
    }

    @Override
    public void writeBody(String str) {
        byte[] bytes = str == null ? new byte[0] : str.getBytes(StandardCharsets.UTF_8);
        setContentLength(bytes.length);
        writeBody(bytes);
    }

    @Override
    public void writeBodyAsJson(String json) {
        setContentType("application/json;charset=utf-8");
        writeBody(json);
    }

    @Override
    public void writeBodyAsJson(Object obj) {
        writeBodyAsJson(JsonUtil.toJson(obj));
    }
}
