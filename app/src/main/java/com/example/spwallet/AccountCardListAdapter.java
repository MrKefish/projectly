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

public class AccountCardListAdapter extends ArrayAdapter<CardDataAccount> {

    final ArrayMap<Integer, Integer> cardsResourceInfo = new ArrayMap<>();

    public AccountCardListAdapter(@NonNull Context context, ArrayList<CardDataAccount> data) {
        super(context, R.layout.account_card_item, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        CardDataAccount cardDataAccount = getItem(position);
        cardsResourceInfo.put(0, R.drawable.cards_blue);
        cardsResourceInfo.put(1, R.drawable.cards_violet);
        cardsResourceInfo.put(2, R.drawable.cards_pink);
        cardsResourceInfo.put(3, R.drawable.cards_red);
        cardsResourceInfo.put(4, R.drawable.cards_orange);
        cardsResourceInfo.put(5, R.drawable.cards_yellow);
        cardsResourceInfo.put(6, R.drawable.cards_green);
        cardsResourceInfo.put(7, R.drawable.cards_cyan);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.account_card_item, parent, false);
        }
        ImageView cardImage = view.findViewById(R.id.cardImage);
        TextView cardNameAccount = view.findViewById(R.id.cardNameAccount);
        TextView cardNumber = view.findViewById(R.id.cardNumber);
        if (cardDataAccount != null) {
            cardNameAccount.setText(cardDataAccount.name);
            cardNumber.setText(cardDataAccount.number);
            if (cardsResourceInfo.get(cardDataAccount.color) != null) {
                cardImage.setImageResource(cardsResourceInfo.get(cardDataAccount.color));
            } else {
                cardImage.setImageResource(cardsResourceInfo.get(0));
            }
        }
        view.setBackgroundColor(Color.parseColor("#00000000"));
        return view;
    }
}
