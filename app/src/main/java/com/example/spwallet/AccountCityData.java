package com.example.spwallet;

public class AccountCityData {
    String name, lane, id, role;
    int netherX, netherZ;
    public AccountCityData(String id, String name, String role, String lane, int netherX, int netherZ) {
        this.id = id;
        this.name = name;
        this.lane = lane;
        this.netherX = netherX;
        this.netherZ = netherZ;
        this.role = role;
    }
}
