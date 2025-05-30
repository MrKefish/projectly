package com.example.spwallet;

import java.util.Comparator;

public class WalletCardDataComparator implements Comparator<WalletCardData> {

    @Override
    public int compare(WalletCardData a, WalletCardData b) {
        return Integer.compare(a.pos, b.pos);
    }
}
