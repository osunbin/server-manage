package com.bin.sm.internal.http.protocol;

import java.util.Map;


public interface HttpResponse {
    /**
     * Retrieves the response status code
     *
     * @return Response status code
     */
    int getStatus();

    /**
     * Sets the response status code
     *
     * @param status Response status code
     * @return Response object
     */
    HttpResponse setStatus(int status);

    /**
     * Adds a response header
     *
     * @param name Header name
     * @param value Header value
     * @return Response object
     */
    HttpResponse addHeader(String name, String value);

    /**
     * Sets a response header
     *
     * @param name Header name
     * @param value Header value
     * @return Response object
     */
    HttpResponse setHeader(String name, String value);

    /**
     * Sets the collection of response headers
     *
     * @param headers Map of headers
     * @return Response object
     */
    HttpResponse setHeaders(Map<String, String> headers);

    /**
     * Sets the content type of the response
     *
     * @param contentType Content type
     * @return Response object
     */
    HttpResponse setContentType(String contentType);

    /**
     * Sets the content length of the response
     *
     * @param size Content length
     * @return Response object
     */
    HttpResponse setContentLength(long size);

    /**
     * Writes the response body as a byte array
     *
     * @param bytes Byte array
     */
    void writeBody(byte[] bytes);

    /**
     * Writes the response body as a string
     *
     * @param str String
     */
    void writeBody(String str);

    /**
     * Writes the response body with an exception
     *
     * @param ex Exception object
     */
    void writeBody(Throwable ex);

    /**
     * Writes the response body as a JSON string
     *
     * @param json JSON string
     */
    void writeBodyAsJson(String json);

    /**
     * Writes the response body as a JSON object
     *
     * @param obj JSON object
     */
    void writeBodyAsJson(Object obj);
}

