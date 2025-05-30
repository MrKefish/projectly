package com.example.spwallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.spwallet.databinding.ActivityLoginLayoutBinding;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;


public class LoginActivity extends AppCompatActivity {
    private String api, id;
    private EditText apiEnterText, idEnterText;
    private String textToBase64;
    private final Context context = this;
    private TextView loginTitle, loginTitle1, loginTitle2, loginTitle3, loginSPWLogoText;
    private Button loginbutton;
    private ImageView loginSPWLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        com.example.spwallet.databinding.ActivityLoginLayoutBinding bindingLoginLayout = ActivityLoginLayoutBinding.inflate(getLayoutInflater());

        setContentView(R.layout.activity_login_layout);
        apiEnterText = findViewById(R.id.apiEnterText);
        idEnterText = findViewById(R.id.idEnterText);
        loginTitle = findViewById(R.id.loginTitle);
        loginTitle1 = findViewById(R.id.loginTitle1);
        loginTitle2 = findViewById(R.id.loginTitle2);
        loginTitle3 = findViewById(R.id.loginTitle3);
        loginbutton = findViewById(R.id.loginbutton);
        loginSPWLogo = findViewById(R.id.loginSPWLogo);
        loginSPWLogoText = findViewById(R.id.loginSPWLogoText);

        loginTitle1.animate().alpha(1f).translationYBy(50f).setStartDelay(400).setDuration(1000);
        loginTitle2.animate().alpha(1f).translationYBy(50f).setStartDelay(400).setDuration(1000);
        loginTitle.animate().alpha(1f).translationYBy(-500f).setStartDelay(1000).setDuration(450);
        idEnterText.animate().alpha(1f).translationYBy(-500f).setStartDelay(1150).setDuration(450);
        loginTitle3.animate().alpha(1f).translationYBy(-500f).setStartDelay(1300).setDuration(450);
        apiEnterText.animate().alpha(1f).translationYBy(-500f).setStartDelay(1450).setDuration(450);
        loginbutton.animate().alpha(1f).translationYBy(-500f).setStartDelay(1600).setDuration(450);
        loginSPWLogo.animate().alpha(1f).translationYBy(-50f).setStartDelay(1750).setDuration(1500);
        loginSPWLogoText.animate().alpha(1f).translationYBy(-50f).setStartDelay(1900).setDuration(1500);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                api = apiEnterText.getText().toString();
                id = idEnterText.getText().toString();
                try {
                    Gson gson = new Gson();
                    FileOutputStream fileOutput = openFileOutput("information.json", MODE_PRIVATE);
                    String strJson = "{cards: [{\"id\": \"" + id + "\", " +
                            "\"token\": \"" + api + "\"}]}";
                    fileOutput.write(strJson.getBytes());
                    fileOutput.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                Intent intent = new Intent(context, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}