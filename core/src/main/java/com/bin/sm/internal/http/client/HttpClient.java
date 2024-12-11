package com.bin.sm.internal.http.client;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.bin.sm.util.JsonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressWarnings("all")
public class HttpClient {




    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();


    /**
     * 开启异步线程调用，且不在意返回结果（实现空callback）
     */
    public static void enqueue(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call arg0, IOException arg1) {

            }

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {

            }

        });
    }

    public static String get(String url) throws IOException {
        Request request = new Request.Builder().get().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    public static <T> T get(String url,Class<T> clazz) throws IOException {
        Request request = new Request.Builder().get().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        return JsonUtil.jsonByteToObject(response.body().bytes(),clazz);
    }

    public static String get(HttpGetRequest httpRequest) throws IOException {
        Request.Builder request = new Request.Builder().get();
        request.url(httpRequest.genGet());
        setHeader(request,httpRequest.getHeader());
        Response response = okHttpClient.newCall(request.build()).execute();
        return response.body().string();
    }

    public static <T> T get(HttpGetRequest httpRequest,Class<T> clazz) throws IOException {
        Request.Builder request = new Request.Builder().get();
        request.url(httpRequest.genGet());
        setHeader(request,httpRequest.getHeader());
        Response response = okHttpClient.newCall(request.build()).execute();
        return JsonUtil.jsonByteToObject(response.body().bytes(),clazz);
    }

    public static void get(HttpGetRequest httpRequest, HttpCallBack<String> callBack) throws IOException {
        Request.Builder request = new Request.Builder().get();
        request.url(httpRequest.genGet());
        setHeader(request,httpRequest.getHeader());
        okHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                Map<String, List<String>> multimap = response.headers().toMultimap();
                callBack.onSuccessful(call, response.body().string());
            }
        });
    }

    public static <T> void get(HttpGetRequest httpRequest,Class<T> clazz,HttpCallBack<T> callBack) throws IOException {
        Request.Builder request = new Request.Builder().get();
        request.url(httpRequest.genGet());
        setHeader(request,httpRequest.getHeader());
        okHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                callBack.onSuccessful(call, JsonUtil.jsonByteToObject(response.body().bytes(),clazz));
            }
        });
    }


    public static <T> T post(String url,Object bodyData,Class<T> clazz) throws IOException {
       return post(url,null,bodyData,clazz);
    }

    public static <T> T post(String url,LinkedHashMap<String, String> header,Object bodyData,Class<T> clazz) throws IOException {
        String jsonData = JsonUtil.toJson(bodyData);
        RequestBody body = RequestBody.create(JSON, jsonData);
        Request.Builder builder = new Request.Builder().url(url).header("Content-Type", "application/json").post(body);
        setHeader(builder,header);
        Response response = okHttpClient.newCall(builder.build()).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return JsonUtil.jsonByteToObject(response.body().bytes(),clazz);
    }



    public static String post(String url,Object bodyData) throws IOException {
        return post(url,null,bodyData);
    }

    public static String post(String url,LinkedHashMap<String, String> header,Object bodyData) throws IOException {
        String jsonData = JsonUtil.toJson(bodyData);
        RequestBody body = RequestBody.create(JSON, jsonData);
        Request.Builder builder = new Request.Builder().url(url).header("Content-Type", "application/json").post(body);
        setHeader(builder,header);
        Response response = okHttpClient.newCall(builder.build()).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return response.body().string();
    }



    private static void setHeader(Request.Builder request, LinkedHashMap<String, String> header) {
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }
}
