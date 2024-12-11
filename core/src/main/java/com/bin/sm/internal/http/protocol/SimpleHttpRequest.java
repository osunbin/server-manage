package com.bin.sm.internal.http.protocol;

import com.bin.sm.internal.http.HttpServerException;
import com.bin.sm.util.JsonUtil;
import com.bin.sm.util.StringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleHttpRequest implements HttpRequest {
    private static final int BODY_BYTE_SIZE = 512;
    private static final Charset DEFAULT_ENCODE = StandardCharsets.UTF_8;
    private final HttpExchange exchange;
    private final Map<String, String> params = new HashMap<>();
    private String originalPath;
    private String path;

    /**
     * Create a SimpleHttpRequest object.
     *
     * @param exchange The HttpExchange object for server communication
     */
    public SimpleHttpRequest(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public URI getUri() {
        return exchange.getRequestURI();
    }

    @Override
    public String getPath() {
        if (path != null) {
            return path;
        }
        String[] array = getOriginalPath().split("/");
        List<String> phases = Stream.of(array).filter(phase -> !phase.isEmpty()).collect(Collectors.toList());
        path = "/" + String.join("/", phases);
        return path;
    }

    @Override
    public String getOriginalPath() {
        if (originalPath != null) {
            return originalPath;
        }
        String uri = getUri().toString();
        originalPath = uri.split("\\?")[0];
        return originalPath;
    }

    @Override
    public String getMethod() {
        return exchange.getRequestMethod();
    }

    @Override
    public String getContentType() {
        return getFirstHeader("Content-Type");
    }

    @Override
    public String getIp() {
        String ip = getFirstHeader("X-Real-IP");
        if (StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = getFirstHeader("X-Forwarded-For");
        }
        return ip;
    }

    @Override
    public String getFirstHeader(String name) {
        List<String> headers = exchange.getRequestHeaders().get(name);
        return headers == null || headers.isEmpty() ? null : headers.getFirst();
    }

    @Override
    public String getFirstHeader(String name, String defaultValue) {
        String value = getFirstHeader(name);
        return value == null ? defaultValue : value;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return exchange.getRequestHeaders();
    }

    @Override
    public String getParam(String name) {
        return getParams().get(name);
    }

    @Override
    public String getParam(String name, String def) {
        return getParams().getOrDefault(name, def);
    }

    @Override
    public Map<String, String> getParams() {
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        if (StringUtil.isEmpty(query)) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyVal = pair.split("=");
            if (keyVal.length > 1) {
                params.put(URLDecoder.decode(keyVal[0], DEFAULT_ENCODE),
                        URLDecoder.decode(keyVal[1], DEFAULT_ENCODE));
            } else {
                params.put(URLDecoder.decode(keyVal[0], DEFAULT_ENCODE), "");
            }
        }

        return params;
    }

    @Override
    public String getBody() throws HttpServerException {
        return getBody(StandardCharsets.UTF_8);
    }

    /**
     * Retrieves the body content.
     */
    @Override
    public String getBody(Charset charset) throws HttpServerException {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getBodyAsStream(), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } catch (IOException e) {
            throw new HttpServerException(500,
                    "Failed to read the body due to an IO error.", e);
        }
        return body.toString();
    }

    @Override
    public <T> T getBody(Class<T> clazz) throws HttpServerException {
        String body = getBody();
        return JsonUtil.fromJson(body, clazz);
    }

    @Override
    public <T> List<T> getBodyAsList(Class<T> clazz) throws HttpServerException {
        String body = getBody();
        // return JSON.parseArray(body, clazz);
        return null;
    }

    @Override
    public byte[] getBodyAsBytes() throws HttpServerException {
        try (InputStream ins = getBodyAsStream();) {
            if (ins == null) {
                return new byte[0];
            }
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                int len;
                byte[] buf = new byte[BODY_BYTE_SIZE];
                while ((len = ins.read(buf)) != -1) {
                    outs.write(buf, 0, len);
                }
                return outs.toByteArray();
            }
        } catch (Exception e) {
            throw new HttpServerException(500, e);
        }
    }

    @Override
    public InputStream getBodyAsStream() {
        return exchange.getRequestBody();
    }
}

