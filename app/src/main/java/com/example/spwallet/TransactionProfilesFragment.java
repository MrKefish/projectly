package com.example.spwallet;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class TransactionProfilesFragment extends Fragment {
    Button backButton;
    Button addButton;
    ListView profilesList;
    Bundle arguments;
    private String cardId;
    private String cardToken;
    TransactionProfiles transactionProfiles;
    Gson gson;
    CardsInformation cardsInformation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_profiles, container, false);
        backButton = view.findViewById(R.id.transactionProfilesBackButton);
        addButton = view.findViewById(R.id.transactionProfilesAddButton);
        profilesList = view.findViewById(R.id.transactionProfilesList);
        gson = new Gson();
        arguments = getArguments();
        assert arguments != null;
        cardId = arguments.getString("cardId");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).replaceFragment("WalletFragment");
            }
        });
        try {
            FileInputStream fileInputStream = getActivity().openFileInput("information.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            cardsInformation = gson.fromJson(inputStreamReader, CardsInformation.class);
            fileInputStream.close();
            for (int i = 0; i < cardsInformation.cards.length; i++) {
                if (Objects.equals(cardsInformation.cards[i].id, cardId)) {cardToken = cardsInformation.cards[i].token; break;}
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            FileInputStream fileInputStream = getActivity().openFileInput("cardsProfiles.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            transactionProfiles = gson.fromJson(inputStreamReader, TransactionProfiles.class);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            try {
                FileOutputStream fileOutputStream = getActivity().openFileOutput("cardsProfiles.json", Context.MODE_PRIVATE);
                transactionProfiles = new TransactionProfiles();
                transactionProfiles.profiles = new TransactionProfiles.TransactionProfile[0];
                fileOutputStream.write(gson.toJson(transactionProfiles).getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (transactionProfiles.profiles.length > 0) {
            ArrayList<TransactionProfiles.TransactionProfile> transactionProfilesArrayList = new ArrayList<>();
            transactionProfilesArrayList.addAll(Arrays.asList(transactionProfiles.profiles));
            TransactionProfilesAdapter transactionProfilesAdapter = new TransactionProfilesAdapter(getActivity(), transactionProfilesArrayList);
            profilesList.setAdapter(transactionProfilesAdapter);
        }
        return view;
    }
    private void addProfile(String profileName, TransactionPostBody[] transactionPosts) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = getActivity().openFileInput("cardsProfiles.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            transactionProfiles = gson.fromJson(inputStreamReader, TransactionProfiles.class);
            fileInputStream.close();
            TransactionProfiles.TransactionProfile[] profiles = new TransactionProfiles.TransactionProfile[transactionProfiles.profiles.length + 1];
            for (int i = 0; i < transactionProfiles.profiles.length; i++) {profiles[i] = transactionProfiles.profiles[i];}
            profiles[transactionProfiles.profiles.length] = new TransactionProfiles.TransactionProfile();
            profiles[transactionProfiles.profiles.length].name = profileName;
            profiles[transactionProfiles.profiles.length].transactionPosts = transactionPosts;
            transactionProfiles.profiles = profiles;
            FileOutputStream fileOutputStream = getActivity().openFileOutput("cardsProfiles.json", Context.MODE_PRIVATE);
            fileOutputStream.write(gson.toJson(transactionProfiles).getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}