package com.example.wojtek.timeorganizer;

/**
 * Created by Wojtek on 22.10.2017.
 */

public class Item {

    private String name;
    private String date;

    public Item(String n, String d) {
        name = n;
        date = d;
    }
    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }
}
