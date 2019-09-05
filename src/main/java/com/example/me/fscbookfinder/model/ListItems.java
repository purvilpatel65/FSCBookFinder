package com.example.me.fscbookfinder.model;

import android.support.annotation.NonNull;

public class ListItems {

    private String titleName;
    private String UID;
    private double priceOfBook;

    public ListItems(){};

    public ListItems(String titleName, double priceOfBook, String uid) {
        this.titleName = titleName;
        this.priceOfBook = priceOfBook;
        this.UID = uid;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public double getPriceOfBook() {
        return priceOfBook;
    }

    public void setPriceOfBook(double priceOfBook) {
        this.priceOfBook = priceOfBook;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
