package com.example.wojtek.timeorganizer;

/**
 * Created by Wojtek on 22.10.2017.
 */

public class Item {

    private String name;
    private String date;

    public Item(String n) {
        name = n;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
