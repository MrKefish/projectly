package com.example.spwallet;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WalletConnectionThread extends Thread {
    String authorization, mode, urltext, numberOfCard;
    Handler handler;
    public WalletConnectionThread(Handler handler, String urltext, String mode, String authorization, String numberOfCard) {
        this.handler = handler;
        this.mode = mode;
        this.authorization = authorization;
        this.urltext = urltext;
        this.numberOfCard = numberOfCard;
    }
    @Override
    public void run() {
        super.run();
//        OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(urltext).newBuilder();
        String url = urlBuilder.build().toString();
        Request.Builder requestBuilder;
        if (authorization == null) {
            requestBuilder = new Request.Builder().url(url)
                    .cacheControl(new CacheControl.Builder().maxStale(30, TimeUnit.DAYS).build());

        } else {
            requestBuilder = new Request.Builder()
                    .url(url)
//                    .header("Host", "spworlds.ru")
                    .cacheControl(new CacheControl.Builder().maxStale(30, TimeUnit.DAYS).build())
                    .addHeader("Authorization", "Bearer " + authorization)
                    .addHeader("Accept", "application/json");
        }
        requestBuilder.setMethod$okhttp(mode);
        Request request = requestBuilder.build();
        // выполняем запрос
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message message = new Message();
                message.arg1 = 0;
                message.arg2 = Integer.parseInt(numberOfCard);
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() & response.code() == 200) {
                    Message message = new Message();
                    String responseData;
                    responseData = response.body().string();
                    message.arg1 = 1;
                    message.arg2 = Integer.parseInt(numberOfCard);
                    message.obj = responseData;
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.arg1 = 0;
                    message.arg2 = Integer.parseInt(numberOfCard);
                    message.obj = response;
                    handler.sendMessage(message);
                }
            }
        });
    }
}
