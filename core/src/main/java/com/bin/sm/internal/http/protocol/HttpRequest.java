package com.bin.sm.internal.http.protocol;

import com.bin.sm.internal.http.HttpServerException;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public interface HttpRequest {

    URI getUri();



    String getPath();



    String getOriginalPath();

    String getMethod();


    String getContentType();


    String getIp();


    String getFirstHeader(String name);



    String getFirstHeader(String name, String defaultValue);


    Map<String, List<String>> getHeaders();



    String getParam(String name);


    String getParam(String name, String defaultValue);

    Map<String, String> getParams();


    String getBody() throws HttpServerException;


    <T> T getBody(Class<T> clazz) throws HttpServerException;



    String getBody(Charset charset) throws HttpServerException;

    byte[] getBodyAsBytes() throws HttpServerException;


    <T> List<T> getBodyAsList(Class<T> clazz) throws HttpServerException;

    InputStream getBodyAsStream();


}
