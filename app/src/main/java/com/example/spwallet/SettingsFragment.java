package com.example.spwallet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SettingsFragment extends Fragment {
    Settings settings;
    Gson gson;
    ImageView profileImageSettingsFragment;
    Bitmap profileImageSmall;
    CheckBox settingsUiProfileImage;
    CheckBox settingsUiProfileInfo;
    CheckBox settingsUiProfileCities;
    MaterialSwitch settingsUiWhiteTheme;
    MaterialSwitch settingsUiExperiments;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        profileImageSettingsFragment = view.findViewById(R.id.profileImageSettingsFragment);
        gson = new Gson();
        try {
            settings = loadSettings();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            FileInputStream imageInputStream = getActivity().openFileInput("profileImageSmall.png");
            profileImageSmall = BitmapFactory.decodeStream(imageInputStream);
            profileImageSettingsFragment.setImageBitmap(profileImageSmall);
        } catch (FileNotFoundException e) {}

        settingsUiProfileImage = view.findViewById(R.id.settingsUiProfileImage);
        settingsUiProfileInfo = view.findViewById(R.id.settingsUiProfileInfo);
        settingsUiProfileCities = view.findViewById(R.id.settingsUiProfileCities);
        settingsUiWhiteTheme = view.findViewById(R.id.settingsUiWhiteTheme);
        settingsUiExperiments = view.findViewById(R.id.settingsUiExperiments);

        settingsUiProfileImage.setChecked(settings.account_settings.profile_photo);
        settingsUiProfileInfo.setChecked(settings.account_settings.profile_info);
        settingsUiProfileCities.setChecked(settings.account_settings.cities);

        settingsUiWhiteTheme.setChecked(settings.light_theme);
        if (settings.experiments) {settingsUiWhiteTheme.setVisibility(View.VISIBLE);} else {settingsUiWhiteTheme.setVisibility(View.GONE);}
        settingsUiExperiments.setChecked(settings.experiments);

        settingsUiProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.account_settings.profile_photo = settingsUiProfileImage.isChecked();
                try {
                    saveSettings(settings);
                } catch (IOException e) {}
            }
        });

        settingsUiProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.account_settings.profile_info = settingsUiProfileInfo.isChecked();
                try {
                    saveSettings(settings);
                } catch (IOException e) {}
            }
        });

        settingsUiProfileCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.account_settings.cities = settingsUiProfileCities.isChecked();
                try {
                    saveSettings(settings);
                } catch (IOException e) {}
            }
        });

        settingsUiWhiteTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.light_theme = settingsUiWhiteTheme.isChecked();
                try {
                    saveSettings(settings);
                    ((MainActivity) getActivity()).updateThemeFromSettings();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        settingsUiExperiments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.experiments = settingsUiExperiments.isChecked();
                try {
                    saveSettings(settings);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (settings.experiments) {settingsUiWhiteTheme.setVisibility(View.VISIBLE);} else {settingsUiWhiteTheme.setVisibility(View.GONE);}
            }
        });

        return view;
    }
    public void saveSettings(Settings settings) throws IOException {
        FileOutputStream fileOutputStream = getActivity().openFileOutput("settings.json", Context.MODE_PRIVATE);
        fileOutputStream.write(gson.toJson(settings).getBytes());
        fileOutputStream.close();
    }
    public Settings loadSettings() throws IOException {
        Settings settings = new Settings();
        FileInputStream fileInputStream = getActivity().openFileInput("settings.json");
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        settings = gson.fromJson(inputStreamReader, Settings.class);
        fileInputStream.close();
        return settings;
    }
}