package com.example.spwallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;


public class SplashActivity extends AppCompatActivity {
    String textToBase64;
    Display display;
    int widthScreen, heightScreen;
    Context context;
    CardsInformation cardsInformation;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1) {
                try {
                    Gson gson = new Gson();
                    Account account = gson.fromJson((String) msg.obj, Account.class);
                    FileOutputStream fileOutputStream = openFileOutput("profile.json", MODE_PRIVATE);
                    fileOutputStream.write(msg.obj.toString().getBytes());
                    fileOutputStream.close();
                    FileOutputStream fileOutputStream1 = openFileOutput("information.json", MODE_PRIVATE);
                    fileOutputStream1.write(gson.toJson(cardsInformation).getBytes());
                    fileOutputStream1.close();
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (FileNotFoundException e) {
                    System.out.println(new RuntimeException(e).getMessage());
                    finish();
                } catch (IOException e) {
                    System.out.println(new RuntimeException(e).getMessage());
                    finish();
                }
            } else if (msg.arg1 == 0) {
                System.out.println(msg.arg1 + "<<< " + msg.obj.toString());
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthScreen = size.x;
        heightScreen = size.y;
        Gson gson = new Gson();
        try {
            FileInputStream fileInput = openFileInput("information.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInput);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            textToBase64 = null;
            cardsInformation = gson.fromJson(bufferedReader, CardsInformation.class);
            if (cardsInformation.cards.length > 0) {
                textToBase64 = cardsInformation.cards[0].id + ":" + cardsInformation.cards[0].token;
                if (textToBase64 != null & !Objects.equals(textToBase64, ":") & !Objects.equals(textToBase64, "null:null")) {
                    context = this;
                    AccountConnectionThread accountConnectionThread = new AccountConnectionThread(handler,
                            "https://spworlds.ru/api/public/accounts/me",
                            Base64.encodeToString(textToBase64.getBytes(), Base64.NO_WRAP),
                            "GET");
                    accountConnectionThread.start();
                } else {
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            } else {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } catch (IOException e) {
            System.out.println("IOException");
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }
}