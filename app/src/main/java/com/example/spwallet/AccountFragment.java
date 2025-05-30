package com.example.spwallet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

public class AccountFragment extends Fragment {
    volatile Account account;
    private Settings settings;
    TextView playerNameText, playerNameStatus, accountCitiesText, playerEnteringDay;
    ListView accountCardsListView, accountCitiesListView;
    LinearLayout linearLayoutInfoAccountFragment;
    ImageView profileImageAccountFragment;
    Bitmap profileImage;
    LinearLayout linearLayoutAccountFragment;
    AccountCardListAdapter accountCardListAdapter;
    AccountCityListAdapter accountCityListAdapter;
    ArrayList<AccountCityData> citiesDataAccount;
    ArrayList<CardDataAccount> cardsDataAccount;
    int citiesTextHeight;

    public AccountFragment() {
        super(R.layout.fragment_account);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerNameText = view.findViewById(R.id.playerNameText);
        profileImageAccountFragment = view.findViewById(R.id.profileImageAccountFragment);
        playerNameStatus = view.findViewById(R.id.playerNameStatus);
        linearLayoutAccountFragment = view.findViewById(R.id.linearLayoutAccountFragment);
        playerEnteringDay = view.findViewById(R.id.playerEnteringDay);
        accountCardsListView = view.findViewById(R.id.accountCardsListView);
        accountCitiesListView = view.findViewById(R.id.accountCitiesListView);
        accountCitiesText = view.findViewById(R.id.accountCitiesText);
        linearLayoutInfoAccountFragment = view.findViewById(R.id.linearLayoutInfoAccountFragment);
        citiesTextHeight = getResources().getDimensionPixelSize(R.dimen.account_cities_text);
        cardsDataAccount = new ArrayList<>();
        citiesDataAccount = new ArrayList<>();
        Gson gson = new Gson();
        try {
            FileInputStream fileInputStream = getActivity().openFileInput("settings.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            settings = gson.fromJson(inputStreamReader, Settings.class);
        } catch (FileNotFoundException e) {}

        try {
            FileInputStream fileInputStream = getActivity().openFileInput("profile.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            try {
                account = gson.fromJson(bufferedReader, Account.class);
                playerNameText.setText(account.username);
                playerNameStatus.setText(account.status);
                for (int i = 0; i < account.cards.length; i++) {
                    cardsDataAccount.add(new CardDataAccount(account.cards[i].name, account.cards[i].number, account.cards[i].color));
                }
                accountCardListAdapter = new AccountCardListAdapter(getActivity(), cardsDataAccount);
                accountCardsListView.setNestedScrollingEnabled(true);
                accountCardsListView.setAdapter(accountCardListAdapter);
                accountCardsListView.setDividerHeight(account.cards.length * accountCardsListView.getDividerHeight());
                int cardListViewHeight = getResources().getDimensionPixelSize(R.dimen.account_card_item);
                LinearLayout.LayoutParams cardlp = (LinearLayout.LayoutParams) accountCardsListView.getLayoutParams();
                cardlp.height = cardListViewHeight * cardsDataAccount.size();
                accountCardsListView.setLayoutParams(cardlp);

                if (account.cities.length > 0 & settings.account_settings.cities) {
                    accountCitiesText.setVisibility(View.VISIBLE);
                    accountCitiesText.setPadding(0, 5, 0, 5);
                    accountCitiesText.setHeight(citiesTextHeight);
                    for (int i = 0; i < account.cities.length; i++) {
                        citiesDataAccount.add(new AccountCityData(account.cities[i].city.id,
                                account.cities[i].city.name,
                                account.cities[i].role,
                                account.cities[i].city.lane,
                                account.cities[i].city.netherX,
                                account.cities[i].city.netherZ));
                    }
                    accountCityListAdapter = new AccountCityListAdapter(getActivity(), citiesDataAccount);
                    accountCitiesListView.setNestedScrollingEnabled(true);
                    accountCitiesListView.setAdapter(accountCityListAdapter);
                    int cityListViewHeight = getResources().getDimensionPixelSize(R.dimen.account_card_item);
                    LinearLayout.LayoutParams citylp = (LinearLayout.LayoutParams) accountCitiesListView.getLayoutParams();
                    citylp.height = cityListViewHeight * citiesDataAccount.size();
                    accountCitiesListView.setLayoutParams(citylp);
                } else {
                    accountCitiesText.setVisibility(View.GONE);
                }

                if (settings.account_settings.profile_info)
                {linearLayoutInfoAccountFragment.setVisibility(View.VISIBLE);}
                else
                {linearLayoutInfoAccountFragment.setVisibility(View.GONE);}

                String[] enteringDate = account.createdAt.split("T")[0].split("-");
                String enteringDay = MessageFormat.format("День входа: {0}", enteringDate[2] + "." + enteringDate[1] + "." + enteringDate[0] + "  Время: " + account.createdAt.split("T")[1].split("Z")[0]);
                playerEnteringDay.setText(enteringDay);
            } catch (com.google.gson.JsonSyntaxException e) {
                System.out.println(new RuntimeException(e).getMessage());
            }
            if (settings.account_settings.profile_photo) {
                FileInputStream imageInputStream = getActivity().openFileInput("profileImage.png");
                profileImage = BitmapFactory.decodeStream(imageInputStream);
                profileImageAccountFragment.setImageBitmap(profileImage);
                profileImageAccountFragment.setVisibility(View.VISIBLE);
            } else {
                profileImageAccountFragment.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            try {
                finalize();
            } catch (Throwable ex) {
                Log.e("NotFinalized", Objects.requireNonNull(ex.getMessage()));
            }
        }
    }
    public void commit() {
        try {
            FileInputStream fileInputStream = getActivity().openFileInput("profile.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            Gson gson = new Gson();
            try {
                account = gson.fromJson(bufferedReader, Account.class);
                playerNameText.setText(account.username);
                playerNameStatus.setText(account.status);

                cardsDataAccount.clear();
                for (int i = 0; i < account.cards.length; i++) {
                    cardsDataAccount.add(new CardDataAccount(account.cards[i].name, account.cards[i].number, account.cards[i].color));
                }
                accountCardListAdapter = new AccountCardListAdapter(getActivity(), cardsDataAccount);
                accountCardsListView.setNestedScrollingEnabled(true);
                accountCardsListView.setAdapter(accountCardListAdapter);
                int baseListViewHeight = getResources().getDimensionPixelSize(R.dimen.account_card_item);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) accountCardsListView.getLayoutParams();
                lp.height = baseListViewHeight * cardsDataAccount.size();
                accountCardsListView.setLayoutParams(lp);

                if (account.cities.length > 0 & settings.account_settings.cities) {
                    citiesDataAccount.clear();
                    accountCitiesText.setVisibility(View.VISIBLE);
                    accountCitiesText.setPadding(0, 5, 0, 5);
                    accountCitiesText.setHeight(citiesTextHeight);
                    for (int i = 0; i < account.cities.length; i++) {
                        citiesDataAccount.add(new AccountCityData(account.cities[i].city.id,
                                account.cities[i].city.name,
                                account.cities[i].role,
                                account.cities[i].city.lane,
                                account.cities[i].city.netherX,
                                account.cities[i].city.netherZ));
                    }


                    if (settings.account_settings.profile_info)
                    {linearLayoutInfoAccountFragment.setVisibility(View.VISIBLE);}
                    else
                    {linearLayoutInfoAccountFragment.setVisibility(View.GONE);}

                    accountCityListAdapter = new AccountCityListAdapter(getActivity(), citiesDataAccount);
                    accountCitiesListView.setNestedScrollingEnabled(true);
                    accountCitiesListView.setAdapter(accountCityListAdapter);
                    int cityListViewHeight = getResources().getDimensionPixelSize(R.dimen.account_card_item);
                    LinearLayout.LayoutParams citylp = (LinearLayout.LayoutParams) accountCitiesListView.getLayoutParams();
                    citylp.height = cityListViewHeight * citiesDataAccount.size();
                    accountCitiesListView.setLayoutParams(citylp);
                } else {
                    accountCitiesText.setVisibility(View.GONE);
                }


                String[] enteringDate = account.createdAt.split("T")[0].split("-");
                String enteringDay = MessageFormat.format("День входа: {0}  Время: {1}", enteringDate[2] + "." + enteringDate[1] + "." + enteringDate[0], account.createdAt.split("T")[1].split("Z")[0]);
                playerEnteringDay.setText(enteringDay);
            } catch (com.google.gson.JsonSyntaxException e) {
                System.out.println(new RuntimeException(e).getMessage());
        }
            if (settings.account_settings.profile_photo) {
                FileInputStream imageInputStream = getActivity().openFileInput("profileImage.png");
                profileImage = BitmapFactory.decodeStream(imageInputStream);
                profileImageAccountFragment.setImageBitmap(profileImage);
                profileImageAccountFragment.setVisibility(View.VISIBLE);
            } else {
                profileImageAccountFragment.setVisibility(View.GONE);
            }

        } catch (IOException e) {
            try {
                finalize();
            } catch (Throwable ex) {
                Log.e("NotFinalized", Objects.requireNonNull(ex.getMessage()));
            }
        }
    }
}