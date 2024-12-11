package com.bin.sm.internal.http;

public class HttpServerException extends RuntimeException {
    /**
     * Private member variable, represents the HTTP status code
     */
    private final int status;

    /**
     * Constructor for creating an HttpServerException instance
     *
     * @param status HTTP status code
     * @param message Exception message
     */
    public HttpServerException(int status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * Constructor for creating an HttpServerException instance with cause
     *
     * @param status HTTP status code
     * @param message Exception message
     * @param cause Original throwable causing the exception
     */
    public HttpServerException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     * Constructor for creating an HttpServerException instance with cause but no explicit message
     *
     * @param status HTTP status code
     * @param cause Original throwable causing the exception
     */
    public HttpServerException(int status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    /**
     * Retrieves the HTTP status code
     *
     * @return HTTP status code
     */
    public int getStatus() {
        return status;
    }
}
