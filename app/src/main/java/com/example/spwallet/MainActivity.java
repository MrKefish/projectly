package com.example.spwallet;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.spwallet.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Account account;
    Settings settings = new Settings();
    ActivityMainBinding activityMainBinding;
    Display display;
    int widthScreen;
    int heightScreen;
    int imageWidth;
    AccountFragment accountFragment;
    WalletFragment walletFragment;
    SettingsFragment settingsFragment;

    final String[] imageModesMCHEADS = {"avatar", "head", "player", "body", "combo"};
    final String[] imageModesSPWORLDS = {"face", "head", "frontbust", "front"};
    final String[] imageModesVZGE = {"face", "front", "frontfull", "head", "bust", "full", "skin", "processedskin"};
    final String avatarsSPW = "https://avatars.spworlds.org/%s/%s?w=%d";
    final String avatarsMCHEADS = "https://mc-heads.net";
    final String avatarsVZGE = "https://vzge.me/%s/%d/%s";

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1) {
                Gson gson = new Gson();
                try {
                    account = gson.fromJson(msg.obj.toString(), Account.class);
                } catch (com.google.gson.JsonSyntaxException e) {
                    showToast("com.google.gson.JsonSyntaxException: " + e.getMessage(), Toast.LENGTH_LONG);
                }
                try {
                    FileOutputStream fileOutputStream = openFileOutput("profile.json", MODE_PRIVATE);
                    fileOutputStream.write(msg.obj.toString().getBytes());
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    System.out.println(new RuntimeException(e).getMessage());
                } catch (IOException e) {
                    System.out.println(new RuntimeException(e).getMessage());
                }
                try {
                    accountFragment.commit();
                } catch (java.lang.NullPointerException e) {
                    System.out.println(new RuntimeException(e).getMessage());
                }
                AccountConnectionThread accountConnectionThread = new AccountConnectionThread(handler,
                        String.format(avatarsVZGE, imageModesVZGE[4], 512, account.minecraftUUID),
                        "OKHTTP");
                accountConnectionThread.start();
            } else if (msg.arg1 == 0) {
                account = null;
            } else if (msg.arg1 == 3) {
                if (msg.arg2 == 0) {
                    Bitmap bitmap = (Bitmap) msg.obj;
                    imageWidth = widthScreen - 40;
                    bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageWidth, false);
                    saveBitmap(bitmap, "profileImage.png");
                    try {
                        accountFragment.commit();
                    } catch (java.lang.NullPointerException e) {
                        System.out.println(new RuntimeException(e).getMessage());
                    }
                } else {
                    Bitmap bitmap = (Bitmap) msg.obj;
                    bitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, false);
                    saveBitmap(bitmap, "profileImageSmall.png");
                }
            } else if (msg.arg1 == 2) {
                if (msg.arg2 == 1) {
                    if (msg.what == 0) {
                        AccountConnectionThread accountConnectionThread = new AccountConnectionThread(handler,
                                String.format(avatarsVZGE, imageModesVZGE[4], 512, account.minecraftUUID),
                                "PICASSO");
                        accountConnectionThread.start();
                    } else {
                        AccountConnectionThread accountConnectionThread = new AccountConnectionThread(handler,
                                String.format(avatarsVZGE, imageModesVZGE[0], 128, account.minecraftUUID),
                                "PICASSO");
                        accountConnectionThread.start();
                    }
                } else {
//                    showToast("Не удалось загрузить фото профиля (small)", Toast.LENGTH_LONG);
                }
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthScreen = size.x;
        heightScreen = size.y;
        accountFragment = new AccountFragment();
        walletFragment = new WalletFragment();
        settingsFragment = new SettingsFragment();
        Gson gson = new Gson();
        try {
            FileInputStream fileInputStream = openFileInput("settings.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            settings = gson.fromJson(inputStreamReader, Settings.class);
        } catch (FileNotFoundException e) {
            try {
                FileOutputStream fileOutputStream = openFileOutput("settings.json", MODE_PRIVATE);
                settings = new Settings();
                settings.experiments = false;
                settings.light_theme = false;
                settings.account_settings = new Settings.Account();
                settings.account_settings.profile_photo = true;
                settings.account_settings.profile_info = true;
                settings.account_settings.cities = true;
                fileOutputStream.write(gson.toJson(settings).getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (settings.light_theme) {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);}
        else {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);}
        try {
            FileInputStream fileInputStream = openFileInput("profile.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            account = gson.fromJson(bufferedReader, Account.class);
            replaceFragment("AccountFragment");
        } catch (FileNotFoundException e) {
            System.out.println(new RuntimeException(e).getMessage());
        }
        if (settings.account_settings.profile_photo) {
            AccountConnectionThread accountConnectionThread = new AccountConnectionThread(handler,
                    String.format(avatarsVZGE, imageModesVZGE[4], 512, account.minecraftUUID),
                    "OKHTTP");
            accountConnectionThread.start();
        }
        AccountConnectionThread accountConnectionThread = new AccountConnectionThread(handler,
                String.format(avatarsVZGE, imageModesVZGE[0], 128, account.minecraftUUID),
                "OKHTTP");
        accountConnectionThread.start();
        activityMainBinding.mainBottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.profileTab) replaceFragment("AccountFragment");
            else if (item.getItemId() == R.id.walletTab) replaceFragment("WalletFragment");
            else if (item.getItemId() == R.id.propertiesTab) replaceFragment("SettingsFragment");
            return true;
        });
    }
    public void updateThemeFromSettings() {
        try {
            settings = loadSettings();
            if (settings.light_theme) {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);}
            else {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);}
            activityMainBinding.mainBottomNavigationView.setSelectedItemId(R.id.profileTab);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void replaceFragment(String fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (Objects.equals(fragment, "AccountFragment")) {
            fragmentTransaction.replace(R.id.mainFragmentContainerView, accountFragment);
        } else if (Objects.equals(fragment, "WalletFragment")) {
            fragmentTransaction.replace(R.id.mainFragmentContainerView, walletFragment);
        } else if (Objects.equals(fragment, "SettingsFragment")) {
            fragmentTransaction.replace(R.id.mainFragmentContainerView, settingsFragment);
        }

        fragmentTransaction.commit();
    }
    public void transactionProfilesOpen(String cardId) {
        Bundle bundle = new Bundle();
        bundle.putString("cardId", cardId);
        TransactionProfilesFragment transactionProfilesFragment = new TransactionProfilesFragment();
        transactionProfilesFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainerView, transactionProfilesFragment);
        fragmentTransaction.commit();
    }
    public void showToast(String text, int time) {
        Toast toast = Toast.makeText(this, text, time);
        toast.show();
    }
    protected void saveBitmap(Bitmap bitmap, String fileName) {
        try {
            FileOutputStream out = openFileOutput(fileName, MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Settings loadSettings() throws IOException {
        Gson gson = new Gson();
        Settings settings = new Settings();
        FileInputStream fileInputStream = openFileInput("settings.json");
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        settings = gson.fromJson(inputStreamReader, Settings.class);
        fileInputStream.close();
        return settings;
    }
}