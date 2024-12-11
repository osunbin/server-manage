package com.bin.sm.internal.http.client;

import okhttp3.Call;

import java.io.IOException;

public interface HttpCallBack<T> {

    void onSuccessful(Call call, T data);

    void onFailure(Call call, IOException ioe);
}
