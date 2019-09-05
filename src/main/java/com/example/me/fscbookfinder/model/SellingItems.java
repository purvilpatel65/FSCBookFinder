package com.example.me.fscbookfinder.model;

public class SellingItems {

    private String bookName;
    private double Price;
    private String KEY;

    public SellingItems(){};

    public SellingItems(String bookName, double price, String key) {
        this.bookName = bookName;
        Price = price;
        this.KEY = key;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getKey() {
        return KEY;
    }

    public void setKey(String key) {
        this.KEY = key;
    }
}
