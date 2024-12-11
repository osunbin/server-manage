package com.bin.sm.extension.http;

import com.bin.sm.router.RouterContext;
import org.springframework.cloud.client.loadbalancer.RequestData;

import java.net.URI;
import java.util.List;

public class HttpRouterContext extends RouterContext {

    private RequestData clientRequest;

    public HttpRouterContext(RequestData clientRequest) {
        super("http");
        this.clientRequest = clientRequest;
    }

    public HttpRouterContext(RequestData clientRequest,String path) {
        super("http",path);
        this.clientRequest = clientRequest;
    }


    public List<String> getCookies(String cookieKey) {
        return clientRequest.getCookies().get(cookieKey);
    }

    public List<String> getHeaders(String headerKey) {
        return clientRequest.getHeaders().get(headerKey);
    }

    public String getPath() {
        return clientRequest.getUrl().getPath();
    }


    public String getAddress() {
        URI url = clientRequest.getUrl();
        return url.getHost() + ":" + url.getPort();
    }

    public String getIp() {
        return clientRequest.getUrl().getHost();
    }


    public List<String> getTag() {
        return clientRequest.getHeaders().get("tag");
    }
}
