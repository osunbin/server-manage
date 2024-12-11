package com.bin.sm.internal.http.client;


import java.util.LinkedHashMap;
import java.util.Map;

public class HttpGetRequest {
    private String url;
    private LinkedHashMap<String, String> header;
    private LinkedHashMap<String, String> params;

    public static HttpGetRequest build() {
        return new HttpGetRequest();
    }

    public static HttpGetRequest build(String url) {
        return new HttpGetRequest(url);
    }

    public HttpGetRequest() {
    }

    public HttpGetRequest(String url) {
        this.url = url;
    }


    public HttpGetRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public HttpGetRequest addHeader(String key, String value) {
        if (header == null) {
            header = new LinkedHashMap<>(16);
        }
        header.put(key, value);
        return this;
    }

    public HttpGetRequest addParam(String key, String value) {
        if (params == null) {
            params = new LinkedHashMap<>(16);
        }
        params.put(key, value);
        return this;
    }

    public String getUrl() {
        return url;
    }

    public LinkedHashMap<String, String> getHeader() {
        return header;
    }


    public String genGet() {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (params != null) {
            urlBuilder.append("?");
            // base64
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(entry.getKey()).
                        append("=").
                        append(entry.getValue()).
                        append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        return urlBuilder.toString();
    }



}
