package com.example.spwallet;

public class Account {
    public String id;
    public String username;
    public String minecraftUUID;
    public String status;
    class Cities {
        public String role;
        public String createdAt;
        class City {
            public String id;
            public String name;
            public int x;
            public int y;
            public int z;
            public int netherX;
            public int netherY;
            public int netherZ;
            public String lane;
        }
        public City city;
    }
    Cities[] cities;
    class Cards {
        public String id;
        public String name;
        public String number;
        public int color;
    }
    Cards[] cards;
    public String createdAt;
}
