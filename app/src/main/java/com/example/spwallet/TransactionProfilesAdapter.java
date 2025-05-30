package com.example.spwallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TransactionProfilesAdapter extends ArrayAdapter<TransactionProfiles.TransactionProfile> {
    TransactionProfiles.TransactionProfile data;
    TextView transactionProfilesItemTitle;
    TextView transactionProfilesItemBody;
    public TransactionProfilesAdapter(@NonNull Context context, ArrayList<TransactionProfiles.TransactionProfile> data) {super(context, R.layout.profile_transaction, data);}

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        data = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.profile_transaction, parent, false);
        }
        transactionProfilesItemTitle = view.findViewById(R.id.transactionProfilesItemTitle);
        transactionProfilesItemTitle.setText(data.name);
        transactionProfilesItemBody = view.findViewById(R.id.transactionProfilesItemBody);
        StringBuilder transactionProfilesItemBodyText = new StringBuilder();
        for (int i = 0; i < data.transactionPosts.length; i++) {
            if (i < 3) {
                transactionProfilesItemBodyText.append("Номер: ").append(data.transactionPosts[i].receiver).append("    Количевство АР: ").append(data.transactionPosts[i].amount).append("    Комментарий: ").append(data.transactionPosts[i].comment).append("\n");
            } else {transactionProfilesItemBodyText.append("..."); break;}
        }
        transactionProfilesItemBody.setText(transactionProfilesItemBodyText);
        return view;
    }
}
