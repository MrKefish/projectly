package com.example.spwallet;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccountConnectionThread extends Thread {
    private String urlText;
    private String authorization;
    private String mode;
    private Handler handler;
    public String imageGetter = "OKHTTP";
    public AccountConnectionThread(Handler handler, String urlText, String authorization, String mode) {
        this.urlText = urlText;
        this.authorization = authorization;
        this.mode = mode;
        this.handler = handler;
    }
    public AccountConnectionThread(Handler handler, String urlText, String imageGetter) {
        this.urlText = urlText;
        this.authorization = null;
        this.mode = "IMAGE";
        this.imageGetter = imageGetter;
        this.handler = handler;
    }

        @Override
    public void run() {
        super.run();
        if (mode != "IMAGE") {
            OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(urlText).newBuilder();
            String url = urlBuilder.build().toString();
            Request.Builder requestBuilder;
            if (authorization == null) {
                requestBuilder = new Request.Builder().url(url)
                        .cacheControl(new CacheControl.Builder().maxStale(30, TimeUnit.DAYS).build());

            } else {
                requestBuilder = new Request.Builder()
//                        .url("https://193.233.15.35/api/public/accounts/me")
                        .url(url)
//                        .header("Host", "spworlds.ru")
                        .cacheControl(new CacheControl.Builder().maxStale(30, TimeUnit.DAYS).build())
                        .addHeader("Authorization", "Bearer " + authorization)
                        .addHeader("Accept", "application/json");
            }
            requestBuilder.setMethod$okhttp(mode);
            Request request = requestBuilder.build();
            // выполняем запрос
            client.newCall(request).enqueue(new Callback() {


                @Override
                public void onResponse(Call call, final Response response) {
                    Message message = new Message();
                    if (!response.isSuccessful() & response.code() != 200) {
                        message.obj = response;
                        System.out.println(request);
                        message.arg1 = 0;
                        handler.sendMessage(message);
                    } else {
                        // читаем данные в отдельном потоке
                        final String responseData;
                        try {
                            responseData = response.body().string();
                        } catch (IOException e) {
                            message.arg1 = 0;
                            message.obj = e;
                            handler.sendMessage(message);
                            return;
                        }
                        System.out.println(responseData);
                        message.obj = responseData;
                        message.arg1 = 1;
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {e.printStackTrace();}
            });

        } else if (Objects.equals(imageGetter, "OKHTTP")) {
            Message message = new Message();
            OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(urlText).addHeader("Accept", "image/bmp").build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {System.out.println(urlText); e.printStackTrace();}

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful() & response.code() != 200) {
                        message.arg1 = 2;
                        message.arg2 = 1;
                        if (Objects.equals(urlText.split("/")[3], "bust")) {
                            message.what = 0;
                        } else {
                            message.what = 1;
                        }
                        System.out.println(response.code());
                        System.out.println(urlText);
                        message.obj = response;
                        handler.sendMessage(message);
                    } else {
                        message.arg1 = 3;
                        if (Objects.equals(urlText.split("/")[3], "bust")) {
                            message.arg2 = 0;
                        } else {
                            message.arg2 = 1;
                        }
                        message.obj = BitmapFactory.decodeStream(response.body().byteStream());
                        handler.sendMessage(message);
                    }
                }
            });
        } else if (Objects.equals(imageGetter, "PICASSO")) {
            Picasso picasso = Picasso.get();
            picasso.setLoggingEnabled(true);
            Message message = new Message();
            System.out.println(urlText);
            try {
                message.obj = picasso.load(urlText).get();
                if (Objects.equals(urlText.split("/")[3], "bust")) {
                    message.what = 0;
                } else {
                    message.what = 1;
                }
                message.arg1 = 3;
            } catch (IOException e) {
                System.out.println(new RuntimeException(e).getMessage() + " \n" + new RuntimeException(e).getCause());
                message.arg1 = 2;
            }
            message.arg2 = 0;
            handler.sendMessage(message);
        }
    }
}