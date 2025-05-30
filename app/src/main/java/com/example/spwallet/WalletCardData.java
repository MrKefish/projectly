package com.example.spwallet;

public class WalletCardData {
    int color, pos;
    String number, name, id, money;
    public WalletCardData(String name, String number, String money, int color, String id, int pos) {
        this.name = name;
        this.number = number;
        this.color = color;
        this.money = money;
        this.id = id;
        this.pos = pos;
    }
}
