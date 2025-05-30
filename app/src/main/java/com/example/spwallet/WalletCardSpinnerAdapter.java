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

public class WalletCardSpinnerAdapter extends ArrayAdapter<WalletCardData> {
    TextView cardNameWallet;
    TextView cardCountWallet;
    TextView cardNumberWallet;
    ImageView cardImage;
    WalletCardData data;
    final ArrayMap<Integer, Integer> cardsResourceInfo = new ArrayMap<>();
    public WalletCardSpinnerAdapter(@NonNull Context context, ArrayList<WalletCardData> data) {super(context, R.layout.wallet_card_item, data);}

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        data = getItem(position);
        cardsResourceInfo.put(0, R.drawable.cards_blue);
        cardsResourceInfo.put(1, R.drawable.cards_violet);
        cardsResourceInfo.put(2, R.drawable.cards_pink);
        cardsResourceInfo.put(3, R.drawable.cards_red);
        cardsResourceInfo.put(4, R.drawable.cards_orange);
        cardsResourceInfo.put(5, R.drawable.cards_yellow);
        cardsResourceInfo.put(6, R.drawable.cards_green);
        cardsResourceInfo.put(7, R.drawable.cards_cyan);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.wallet_card_item, parent, false);
        }
        cardImage = view.findViewById(R.id.cardImage);
        cardNameWallet = view.findViewById(R.id.cardNameWallet);
        cardNumberWallet = view.findViewById(R.id.cardNumberWallet);
        cardCountWallet = view.findViewById(R.id.cardCountWallet);
        cardNameWallet.setText(data.name);
        cardNumberWallet.setText(data.number);
        view.setBackgroundColor(Color.parseColor("#00000000"));
        if (cardsResourceInfo.get(data.color) != null) {
            cardImage.setImageResource(cardsResourceInfo.get(data.color));
        } else {
            cardImage.setImageResource(cardsResourceInfo.get(0));
        }
        cardCountWallet.setText(data.money + " АР");
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        data = getItem(position);
        cardsResourceInfo.put(0, R.drawable.cards_blue);
        cardsResourceInfo.put(1, R.drawable.cards_violet);
        cardsResourceInfo.put(2, R.drawable.cards_pink);
        cardsResourceInfo.put(3, R.drawable.cards_red);
        cardsResourceInfo.put(4, R.drawable.cards_orange);
        cardsResourceInfo.put(5, R.drawable.cards_yellow);
        cardsResourceInfo.put(6, R.drawable.cards_green);
        cardsResourceInfo.put(7, R.drawable.cards_cyan);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.wallet_card_item, parent, false);
        }
        cardImage = view.findViewById(R.id.cardImage);
        cardNameWallet = view.findViewById(R.id.cardNameWallet);
        cardNumberWallet = view.findViewById(R.id.cardNumberWallet);
        cardCountWallet = view.findViewById(R.id.cardCountWallet);
        cardNameWallet.setText(data.name);
        cardNumberWallet.setText(data.number);
        view.setBackgroundColor(Color.parseColor("#00000000"));
        if (cardsResourceInfo.get(data.color) != null) {
            cardImage.setImageResource(cardsResourceInfo.get(data.color));
        } else {
            cardImage.setImageResource(cardsResourceInfo.get(0));
        }
        cardCountWallet.setText(data.money + " АР");
        return view;
    }
}
