package com.bin.sm.internal.http.protocol;

public class ResponseResult<T> {
    private boolean success;

    private String message;

    private T data;

    /**
     * Constructor for creating a ResponseResult object.
     *
     * @param success Indicates whether the operation was successful
     * @param message Prompt message on success or failure
     * @param data Data returned upon success
     */
    public ResponseResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Creates a successful ResponseResult object.
     *
     * @param <T> Generic type
     * @return ResponseResult object
     */
    public static <T> ResponseResult<T> ofSuccess() {
        return new ResponseResult<>(true, null, null);
    }

    /**
     * Creates a successful ResponseResult object with specified data.
     *
     * @param <T> Generic type
     * @param data Data
     * @return ResponseResult object
     */
    public static <T> ResponseResult<T> ofSuccess(T data) {
        return new ResponseResult<>(true, null, data);
    }

    /**
     * Creates a failed ResponseResult object.
     *
     * @param <T> Generic type
     * @return ResponseResult object
     */
    public static <T> ResponseResult<T> ofFailure() {
        return new ResponseResult<>(false, null, null);
    }

    /**
     * Creates a failed ResponseResult object with specified data.
     *
     * @param <T> Generic type
     * @param data Data
     * @return ResponseResult object
     */
    public static <T> ResponseResult<T> ofFailure(T data) {
        return new ResponseResult<>(false, null, data);
    }

    /**
     * Creates a failed ResponseResult object with specified exception information.
     *
     * @param <T> Generic type
     * @param t Exception information
     * @return ResponseResult object
     */
    public static <T> ResponseResult<T> ofFailure(Throwable t) {
        return new ResponseResult<>(false, t.getMessage(), null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

