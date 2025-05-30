package com.example.spwallet;

import android.content.Context;
import android.graphics.Color;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class AccountCityListAdapter extends ArrayAdapter<AccountCityData> {
    TextView cityNameAccount;
    TextView cityCoordAccount;
    ImageView cityImageAccount;
    TextView cityRoleAccount;
    final ArrayMap<String, Integer> cityResourceInfo = new ArrayMap<>();
    final ArrayMap<String, String> cityColorInfo = new ArrayMap<>();
    final ArrayMap<String, String> cityNameInfo = new ArrayMap<>();

    public AccountCityListAdapter(@NonNull Context context, ArrayList<AccountCityData> data) {
        super(context, R.layout.account_city_item, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        AccountCityData accountCityData = getItem(position);
        cityResourceInfo.put("green", R.drawable.city_green);
        cityResourceInfo.put("red", R.drawable.city_red);
        cityResourceInfo.put("yellow", R.drawable.city_yellow);
        cityResourceInfo.put("blue", R.drawable.city_blue);
        cityColorInfo.put("green", "#43af68");
        cityColorInfo.put("red", "#ff5353");
        cityColorInfo.put("yellow", "#ffbd3e");
        cityColorInfo.put("blue", "#4277ff");
        cityNameInfo.put("green", "Зеленая");
        cityNameInfo.put("red", "Красная");
        cityNameInfo.put("yellow", "Жёлтая");
        cityNameInfo.put("blue", "Синяя");

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.account_city_item, parent, false);
        }
        cityNameAccount = view.findViewById(R.id.cityNameAccount);
        cityImageAccount = view.findViewById(R.id.cityImageAccount);
        cityCoordAccount = view.findViewById(R.id.cityCoordAccount);
        cityRoleAccount = view.findViewById(R.id.cityRoleAccount);

        cityNameAccount.setText(accountCityData.name.strip());
        if (cityResourceInfo.get(accountCityData.lane) != null) {
            cityImageAccount.setImageResource(cityResourceInfo.get(accountCityData.lane));
        } else {
            cityImageAccount.setImageResource(R.drawable.city_error);
        }
        if (cityColorInfo.get(accountCityData.lane) != null) {
            if (Objects.equals(accountCityData.lane, "green") | Objects.equals(accountCityData.lane, "blue")) {
                cityCoordAccount.setText(cityNameInfo.get(accountCityData.lane) + " " + Math.abs(accountCityData.netherX));
                cityCoordAccount.setTextColor(Color.parseColor(cityColorInfo.get(accountCityData.lane)));
            } else if (Objects.equals(accountCityData.lane, "yellow") | Objects.equals(accountCityData.lane, "red")) {
                cityCoordAccount.setText(cityNameInfo.get(accountCityData.lane) + " " + Math.abs(accountCityData.netherZ));
                cityCoordAccount.setTextColor(Color.parseColor(cityColorInfo.get(accountCityData.lane)));
            }
        }
        if (Objects.equals(accountCityData.role, "member")) {
            cityRoleAccount.setText("• Житель");
        } else if (accountCityData.role != null) {
            cityRoleAccount.setText("• " + accountCityData.role);
        }

        return view;
    }
}
