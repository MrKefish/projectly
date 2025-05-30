package com.example.spwallet;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class WalletAccountTransactionAdapter  extends ArrayAdapter<String> {
    TextView walletAccountTransactionSpinnerItemName;
    public WalletAccountTransactionAdapter(@NonNull Context context, ArrayList<String> data) {super(context, R.layout.account_transaction_spinner_item, data);}
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.account_transaction_spinner_item_no_stroke, parent, false);
        walletAccountTransactionSpinnerItemName = view.findViewById(R.id.walletAccountTransactionSpinnerItemName);
        walletAccountTransactionSpinnerItemName.setText(getItem(position));
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.account_transaction_spinner_item, parent, false);
        }
        walletAccountTransactionSpinnerItemName = view.findViewById(R.id.walletAccountTransactionSpinnerItemName);
        walletAccountTransactionSpinnerItemName.setText(getItem(position));
        view.setBackgroundColor(Color.parseColor("#00000000"));
        return view;
    }
}
