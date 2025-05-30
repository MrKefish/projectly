package com.example.spwallet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class WalletFragment extends Fragment {
    ArrayList<WalletCardData> walletCardData;
    Spinner walletCardsSpinner;
    CircularProgressIndicator circularProgressIndicator;
    Account account;
    Settings settings;
    LinearLayout walletLoadingLayout;
    LinearLayout walletContentLayout;
    LinearLayout walletCardContent;
    LinearLayout walletCardNoToken;
    ConstraintLayout walletContentToolsLayout;
    Button walletNumberTransactionButton;
    Button walletAddTokenButton;
    Button changeToken;
    Button walletNumberDeleteTokenButton;
    Button walletAccountTransactionButton;
    Button transactionProfilesButton;
    GridLayout walletFirstToolsLayout;
    int dialogWidth;
    private String authorization;
    private CardsInformation cardsInformation;
    private final Map<String,String> cardsBalance = new HashMap<>();
    public interface TransactionService {
        @POST("sapi/public/transaction")
        Call<TransactionPostResponse> transaction(@Header("Authorization") String authorization, @Body TransactionPostBody transactionPostBody);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 0) {
                cardsBalance.put(String.valueOf(msg.arg2), "***");
            } else {
                cardsBalance.put(String.valueOf(msg.arg2), String.valueOf(msg.obj).split(",")[0].split(":")[1]);
            }
            walletCardData.clear();
            for (int i = 0; i < account.cards.length; i++) {
                String count = cardsBalance.get(account.cards[i].number);
                if (count == null) {
                    count = "***";
                }
                walletCardData.add(new WalletCardData(account.cards[i].name,
                        account.cards[i].number,
                        count,
                        account.cards[i].color,
                        account.cards[i].id,
                        i));
            }
            try {
                WalletCardSpinnerAdapter walletCardSpinnerAdapter = new WalletCardSpinnerAdapter(getActivity(), walletCardData);
                walletCardSpinnerAdapter.sort(new WalletCardDataComparator());
                walletCardsSpinner.setAdapter(walletCardSpinnerAdapter);
                walletCardsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        for (Account.Cards x: account.cards) {
                            if (String.valueOf(((WalletCardData) adapterView.getItemAtPosition(i)).number).equals(x.number)) {
                                for (CardsInformation.Card y: cardsInformation.cards) {
                                    if (Objects.equals(x.id, y.id)) {
                                        walletCardNoToken.setVisibility(View.GONE);
                                        walletCardContent.setVisibility(View.VISIBLE);
                                        walletContentToolsLayout.setVisibility(View.VISIBLE);

                                        if (settings.experiments) {
                                            walletFirstToolsLayout.setVisibility(View.VISIBLE);
                                        } else {
                                            walletFirstToolsLayout.setVisibility(View.GONE);
                                        }

                                        transactionProfilesButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                ((MainActivity) getActivity()).transactionProfilesOpen(x.id);
                                            }
                                        });

                                        walletNumberTransactionButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Dialog dialog = new Dialog(getActivity());
                                                dialog.setContentView(R.layout.number_transaction);
                                                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_dialog);
                                                dialog.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                Button okayBtn = dialog.findViewById(R.id.walletNumberTransactionButtonOk);
                                                Button cancelBtn = dialog.findViewById(R.id.walletNumberTransactionButtonCancel);
                                                Button exitBtn = dialog.findViewById(R.id.walletNumberTransactionCloseButton);
                                                EditText number = dialog.findViewById(R.id.walletNumberTransactionEnter);
                                                EditText count = dialog.findViewById(R.id.walletNumberTransactionCountEnter);
                                                EditText comment = dialog.findViewById(R.id.walletNumberTransactionCommentEnter);
                                                MaterialSwitch anonymousModeSwitch = dialog.findViewById(R.id.walletAnonymousModeSwitch);
                                                cancelBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                exitBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                okayBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                        if (!String.valueOf(count.getText()).equals("") & !String.valueOf(number.getText()).equals("")) {
                                                            String receiver = String.valueOf(number.getText());
                                                            int amount = Integer.parseInt(String.valueOf(count.getText()));
                                                            String commentText;
                                                            if (!String.valueOf(comment.getText()).isEmpty()) {
                                                                if (anonymousModeSwitch.isChecked()) {
                                                                    commentText = "Некто: " + String.valueOf(comment.getText());
                                                                } else {
                                                                    commentText = account.username + ": " + String.valueOf(comment.getText());
                                                                }
                                                            } else {
                                                                if (anonymousModeSwitch.isChecked()) {
                                                                    commentText = "Анонимный перевод";
                                                                } else {
                                                                    commentText = account.username;
                                                                }
                                                            }
                                                            String cardID = ((WalletCardData) adapterView.getItemAtPosition(i)).id;
                                                            makeTransaction(receiver, amount, commentText, cardID);
                                                        }
                                                    }
                                                });

                                                dialog.setCancelable(true);
                                                dialog.show();
                                            }
                                        });
                                        changeToken.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Dialog dialog = new Dialog(getActivity());
                                                dialog.setContentView(R.layout.add_token);
                                                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_dialog);
                                                dialog.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                dialog.setCancelable(true);
                                                TextView walletAddTokenTitle = dialog.findViewById(R.id.walletAddTokenTitle);
                                                walletAddTokenTitle.setText("Изменение токена карты " + ((WalletCardData) adapterView.getItemAtPosition(i)).number);
                                                EditText walletAddTokenEnter = dialog.findViewById(R.id.walletAddTokenEnter);
                                                Button close = dialog.findViewById(R.id.walletAddTokenCloseButton);
                                                Button cancel = dialog.findViewById(R.id.walletAddTokenButtonCancel);

                                                close.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                Button ok = dialog.findViewById(R.id.walletAddTokenButtonOk);

                                                ok.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Gson gson = new Gson();
                                                        try {
                                                            FileInputStream fileInputStream = getActivity().openFileInput("information.json");
                                                            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                                                            cardsInformation = gson.fromJson(inputStreamReader, CardsInformation.class);
                                                            inputStreamReader.close();
                                                            if (!String.valueOf(walletAddTokenEnter.getText()).isEmpty()) {
                                                                for (Account.Cards x: account.cards) {if (((WalletCardData) adapterView.getItemAtPosition(i)).number.equals(x.number)) {
                                                                    boolean isThere = false;
                                                                    for (int i = 0; i < cardsInformation.cards.length; i++) {
                                                                        if (Objects.equals(cardsInformation.cards[i].id, x.id)) {
                                                                            cardsInformation.cards[i].token = String.valueOf(walletAddTokenEnter.getText());
                                                                            isThere = true;
                                                                            break;
                                                                        }
                                                                    }

                                                                    if (!isThere) {
                                                                        CardsInformation.Card[] cards = new CardsInformation.Card[cardsInformation.cards.length + 1];
                                                                        for (int i = 0; i < cardsInformation.cards.length; i++) {
                                                                            cards[i] = cardsInformation.cards[i];
                                                                        }
                                                                        CardsInformation.Card card = new CardsInformation.Card();
                                                                        card.id = x.id;
                                                                        card.token = String.valueOf(walletAddTokenEnter.getText());
                                                                        cards[cards.length - 1] = card;
                                                                        cardsInformation.cards = cards;
                                                                    }
                                                                    dialog.dismiss();
                                                                    FileOutputStream fileOutputStream = getActivity().openFileOutput("information.json", Context.MODE_PRIVATE);
                                                                    fileOutputStream.write(gson.toJson(cardsInformation).getBytes());
                                                                    fileOutputStream.close();
                                                                    updateFragment();
                                                                    break;
                                                                }}
                                                            } else {
                                                                dialog.dismiss();
                                                                updateFragment();
                                                                Toast.makeText(getActivity(), "Не введён новый токен для карты " + ((WalletCardData) adapterView.getItemAtPosition(i)).number, Toast.LENGTH_LONG).show();
                                                            }

                                                        } catch (FileNotFoundException e) {
                                                            throw new RuntimeException(e);
                                                        } catch (IOException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                                });
                                                dialog.show();
                                            }
                                        });

                                        walletNumberDeleteTokenButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Dialog dialog = new Dialog(getActivity());
                                                dialog.setContentView(R.layout.delete_token);
                                                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_dialog);
                                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                dialog.setCancelable(true);
                                                Button cancel = dialog.findViewById(R.id.walletDeleteTokenButtonCancel);
                                                TextView title = dialog.findViewById(R.id.walletDeleteTokenTitle);
                                                TextView walletDeleteTokenWarning = dialog.findViewById(R.id.walletDeleteTokenWarning);
                                                title.setText("Вы уверены, что хотите\nудалить токен карты " + String.valueOf(((WalletCardData) adapterView.getItemAtPosition(i)).number) + "?");
                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                Button ok = dialog.findViewById(R.id.walletDeleteTokenButtonOk);
                                                Gson gson = new Gson();
                                                FileInputStream fileInputStream = null;
                                                try {
                                                    fileInputStream = getActivity().openFileInput("information.json");
                                                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                                                    cardsInformation = gson.fromJson(inputStreamReader, CardsInformation.class);
                                                    inputStreamReader.close();
                                                    if (cardsInformation.cards.length < 2) {walletDeleteTokenWarning.setVisibility(View.VISIBLE);}
                                                    else {walletDeleteTokenWarning.setVisibility(View.GONE);}
                                                } catch (FileNotFoundException e) {
                                                    throw new RuntimeException(e);
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }

                                                ok.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        try {
                                                            FileInputStream fileInputStream = getActivity().openFileInput("information.json");
                                                            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                                                            cardsInformation = gson.fromJson(inputStreamReader, CardsInformation.class);
                                                            inputStreamReader.close();
                                                            CardsInformation.Card[] cards = new CardsInformation.Card[cardsInformation.cards.length - 1];
                                                            int k = 0;
                                                            for (int i = 0; i < cards.length; i++) {
                                                                if (!Objects.equals(cardsInformation.cards[k].id, x.id)) {
                                                                    cards[i] = cardsInformation.cards[k];
                                                                    k++;
                                                                } else {
                                                                    k++;
                                                                    cards[i] = cardsInformation.cards[k];
                                                                }
                                                            }
                                                            cardsInformation.cards = cards;
                                                            FileOutputStream fileOutputStream = getActivity().openFileOutput("information.json", Context.MODE_PRIVATE);
                                                            fileOutputStream.write(gson.toJson(cardsInformation).getBytes());
                                                            fileOutputStream.close();
                                                            if (cardsInformation.cards.length < 1) {
                                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                                startActivity(intent);
                                                                getActivity().finish();
                                                            }
                                                            updateFragment();
                                                        } catch (FileNotFoundException e) {
                                                            throw new RuntimeException(e);
                                                        } catch (IOException e) {
                                                            throw new RuntimeException(e);
                                                        }

                                                        dialog.dismiss();
                                                    }
                                                });
                                                dialog.show();
                                            }
                                        });
                                        walletAccountTransactionButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Dialog dialog = new Dialog(getActivity());
                                                dialog.setContentView(R.layout.account_transaction);
                                                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_dialog);
                                                dialog.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                dialog.setCancelable(true);
                                                Spinner cardChooser = dialog.findViewById(R.id.walletAccountTransactionSpinner);
                                                ArrayList<String> cardChooserList = new ArrayList<>();
                                                for (Account.Cards x: account.cards) {cardChooserList.add(x.name);}
                                                WalletAccountTransactionAdapter cardChooserAdapter = new WalletAccountTransactionAdapter(getContext(), cardChooserList);
                                                cardChooser.setAdapter(cardChooserAdapter);
                                                Button close = dialog.findViewById(R.id.walletAccountTransactionCloseButton);
                                                Button cancel = dialog.findViewById(R.id.walletAccountTransactionButtonCancel);
                                                Button ok = dialog.findViewById(R.id.walletAccountTransactionButtonOk);
                                                EditText amountEnter = dialog.findViewById(R.id.walletAccountTransactionAmountEnter);

                                                ok.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                        String cardName = (String) (cardChooser.getSelectedItem());
                                                        String receiver = "";
                                                        if (!String.valueOf(amountEnter.getText()).equals("")) {
                                                            int amount = Integer.parseInt(String.valueOf(amountEnter.getText()));
                                                            for (Account.Cards cardX: account.cards) {
                                                                if (Objects.equals(cardX.name, cardName)) {
                                                                    receiver = cardX.number;
                                                                    break;
                                                                }
                                                            }
                                                            if (!receiver.isEmpty()) {
                                                                makeTransaction(receiver, amount, "Перевод с " + x.number, x.id);
                                                            }
                                                        }
                                                    }
                                                });

                                                close.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                dialog.show();
                                            }
                                        });
                                        break;
                                    }  else {
                                        walletCardContent.setVisibility(View.GONE);
                                        walletContentToolsLayout.setVisibility(View.GONE);
                                        walletCardNoToken.setVisibility(View.VISIBLE);
                                        walletAddTokenButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Dialog dialog = new Dialog(getActivity());
                                                dialog.setContentView(R.layout.add_token);
                                                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_dialog);
                                                dialog.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                dialog.setCancelable(true);
                                                TextView walletAddTokenTitle = dialog.findViewById(R.id.walletAddTokenTitle);
                                                walletAddTokenTitle.setText("Добавление токена к карте " + ((WalletCardData) adapterView.getItemAtPosition(i)).number);
                                                EditText walletAddTokenEnter = dialog.findViewById(R.id.walletAddTokenEnter);
                                                Button close = dialog.findViewById(R.id.walletAddTokenCloseButton);
                                                Button cancel = dialog.findViewById(R.id.walletAddTokenButtonCancel);

                                                close.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                Button ok = dialog.findViewById(R.id.walletAddTokenButtonOk);

                                                ok.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Gson gson = new Gson();
                                                        try {
                                                            FileInputStream fileInputStream = getActivity().openFileInput("information.json");
                                                            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                                                            cardsInformation = gson.fromJson(inputStreamReader, CardsInformation.class);
                                                            if (!String.valueOf(walletAddTokenEnter.getText()).isEmpty()) {
                                                                for (Account.Cards x: account.cards) {if (((WalletCardData) adapterView.getItemAtPosition(i)).number.equals(x.number)) {
                                                                    boolean isThere = false;
                                                                    for (int i = 0; i < cardsInformation.cards.length; i++) {
                                                                        if (Objects.equals(cardsInformation.cards[i].id, x.id)) {
                                                                            cardsInformation.cards[i].token = String.valueOf(walletAddTokenEnter.getText());
                                                                            isThere = true;
                                                                            break;
                                                                        }
                                                                    }

                                                                    if (!isThere) {
                                                                        CardsInformation.Card[] cards = new CardsInformation.Card[cardsInformation.cards.length + 1];
                                                                        for (int i = 0; i < cardsInformation.cards.length; i++) {
                                                                            cards[i] = cardsInformation.cards[i];
                                                                        }
                                                                        CardsInformation.Card card = new CardsInformation.Card();
                                                                        card.id = x.id;
                                                                        card.token = String.valueOf(walletAddTokenEnter.getText());
                                                                        cards[cards.length - 1] = card;
                                                                        cardsInformation.cards = cards;
                                                                    }
                                                                    dialog.dismiss();
                                                                    FileOutputStream fileOutputStream = getActivity().openFileOutput("information.json", Context.MODE_PRIVATE);
                                                                    fileOutputStream.write(gson.toJson(cardsInformation).getBytes());
                                                                    fileOutputStream.close();
                                                                    updateFragment();
                                                                    break;
                                                                }}
                                                            } else {
                                                                dialog.dismiss();
                                                                updateFragment();
                                                                Toast.makeText(getActivity(), "Не введён токен для карты " + ((WalletCardData) adapterView.getItemAtPosition(i)).number, Toast.LENGTH_LONG).show();
                                                            }

                                                        } catch (FileNotFoundException e) {
                                                            throw new RuntimeException(e);
                                                        } catch (IOException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                                });

                                                dialog.show();
                                            }
                                        });
                                    }
                                }
                                break;
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                walletContentLayout.setVisibility(View.VISIBLE);
                walletLoadingLayout.setVisibility(View.GONE);
            } catch (NullPointerException e) {
                System.out.println(new RuntimeException(e).getMessage());
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        walletCardData = new ArrayList<>();
        circularProgressIndicator = view.findViewById(R.id.walletLoadingIndicator);
        circularProgressIndicator.setTrackThickness(10000000);
        circularProgressIndicator.setIndeterminate(true);
        walletContentLayout = view.findViewById(R.id.walletContentLayout);
        walletLoadingLayout = view.findViewById(R.id.walletLoadingLayout);
        walletCardsSpinner = view.findViewById(R.id.walletCardsSpinner);
        walletCardContent = view.findViewById(R.id.walletCardContent);
        walletCardNoToken = view.findViewById(R.id.walletCardNoToken);
        walletNumberTransactionButton = view.findViewById(R.id.walletNumberTransactionButton);
        walletAddTokenButton = view.findViewById(R.id.walletAddTokenButton);
        changeToken = view.findViewById(R.id.walletNumberChangeTokenButton);
        walletNumberDeleteTokenButton = view.findViewById(R.id.walletNumberDeleteTokenButton);
        walletAccountTransactionButton = view.findViewById(R.id.walletAccountTransactionButton);
        walletContentToolsLayout = view.findViewById(R.id.walletContentToolsLayout);
        walletFirstToolsLayout = view.findViewById(R.id.walletFirstToolsLayout);
        transactionProfilesButton = view.findViewById(R.id.transactionProfilesButton);
        walletCardNoToken.setVisibility(View.GONE);
        walletContentLayout.setVisibility(View.GONE);
        walletCardContent.setVisibility(View.GONE);
        walletLoadingLayout.setVisibility(View.VISIBLE);
        walletContentToolsLayout.setVisibility(View.GONE);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        dialogWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) (size.x / 2.35), getResources().getDisplayMetrics());
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
            account = gson.fromJson(bufferedReader, Account.class);
            FileInputStream fileInputStream1 = getActivity().openFileInput("information.json");
            InputStreamReader inputStreamReader1 = new InputStreamReader(fileInputStream1);
            BufferedReader bufferedReader1 = new BufferedReader(inputStreamReader1);
            cardsInformation = gson.fromJson(bufferedReader1, CardsInformation.class);
            authorization = cardsInformation.cards[0].id + ":" + cardsInformation.cards[0].token;
            updateFragment();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return view;
    }
    public void updateFragment() {
        cardsBalance.clear();
        walletLoadingLayout.setVisibility(View.VISIBLE);
        walletContentLayout.setVisibility(View.GONE);
        walletContentToolsLayout.setVisibility(View.GONE);
        for (int i = 0; i < cardsInformation.cards.length; i++) {
            authorization = cardsInformation.cards[i].id + ":" + cardsInformation.cards[i].token;
            String number = "*****";
            for (Account.Cards x: account.cards) {
                if (Objects.equals(x.id, cardsInformation.cards[i].id)) {
                    number = x.number;
                }
            }
            WalletConnectionThread walletConnectionThread = new WalletConnectionThread(handler,
                    "https://spworlds.ru/api/public/card",
                    "GET",
                    Base64.encodeToString(authorization.getBytes(), Base64.NO_WRAP),
                    number);
            walletConnectionThread.start();
        }
    }

    public void makeTransaction(String receiver, int amount, String comment, String cardId) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://spworlds.ru").addConverterFactory(GsonConverterFactory.create()).build();
        TransactionService transactionService = retrofit.create(TransactionService.class);
        TransactionPostBody transactionPostBody = new TransactionPostBody();
        transactionPostBody.receiver = receiver;
        transactionPostBody.amount = amount;
        transactionPostBody.comment = comment;
        for (int i = 0; i < cardsInformation.cards.length; i++) {
            if (Objects.equals(cardId, cardsInformation.cards[i].id)) {
                authorization = cardsInformation.cards[i].id + ":" + cardsInformation.cards[i].token;
                break;
            }
        }
        Call<TransactionPostResponse> transactionCall = transactionService.transaction("Bearer " + Base64.encodeToString(authorization.getBytes(), Base64.NO_WRAP), transactionPostBody);
        transactionCall.enqueue(new Callback<TransactionPostResponse>() {
            @Override
            public void onResponse(Call<TransactionPostResponse> call, Response<TransactionPostResponse> response) {
                if (response.isSuccessful() & response.code() == 201) {
                    Dialog dialogDone = new Dialog(getActivity());
                    dialogDone.setContentView(R.layout.transaction_done);
                    Objects.requireNonNull(dialogDone.getWindow()).setBackgroundDrawableResource(R.drawable.round_dialog);
                    dialogDone.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialogDone.setCancelable(true);
                    TextView messageText = dialogDone.findViewById(R.id.walletTransactionDoneMessage);
                    if (comment.isEmpty()) {
                        messageText.setText("Перевод на " + receiver + "\n" + amount + " Ар");
                    } else {
                        messageText.setText("Перевод на " + receiver + "\n" + amount + " Ар\nКомментарий: " + comment);
                    }
                    Button ok = dialogDone.findViewById(R.id.walletTransactionDoneOk);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateFragment();
                            dialogDone.dismiss();
                        }
                    });
                    dialogDone.show();

                } else {
                    Dialog dialogFailture = new Dialog(getActivity());
                    dialogFailture.setContentView(R.layout.transaction_failture);
                    Objects.requireNonNull(dialogFailture.getWindow()).setBackgroundDrawableResource(R.drawable.round_dialog);
                    dialogFailture.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialogFailture.setCancelable(false);
                    TextView errorText = dialogFailture.findViewById(R.id.walletTransactionFailtureError);
                    String responseCode = String.valueOf(response.code());
                    errorText.setText("Ошибка " + responseCode);
                    Button ok = dialogFailture.findViewById(R.id.walletTransactionFailtureOk);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateFragment();
                            dialogFailture.dismiss();
                        }
                    });
                    dialogFailture.show();
                }
            }

            @Override
            public void onFailure(Call<TransactionPostResponse> call, Throwable t) {
                Dialog dialogFailture = new Dialog(getActivity());
                dialogFailture.setContentView(R.layout.transaction_failture);
                Objects.requireNonNull(dialogFailture.getWindow()).setBackgroundDrawableResource(R.drawable.round_dialog);
                dialogFailture.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogFailture.setCancelable(false);
                Button ok = dialogFailture.findViewById(R.id.walletTransactionFailtureOk);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateFragment();
                        dialogFailture.dismiss();
                    }
                });
                dialogFailture.show();
            }
        });
    }
}