package com.example.wojtek.timeorganizer;

/**
 * Created by Wojtek on 22.10.2017.
 */

public class Item {

    private String name;
    private String date;
    private String id;

    public Item(String n, String d, String i) {
        name = n;
        date = d;
        id = i;
    }
    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }


    public void setName(String name) {
        this.name = name;
    }
}
