package com.example.me.fscbookfinder.model;

public class Data {

    private String UID, uName, name;
    private float Rating;

    public Data(){};

    public Data(String UID, String uName, String name, float rating) {
        this.UID = UID;
        this.uName = uName;
        this.name = name;
        this.Rating = rating;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return Rating;
    }

    public void setRating(float rating) {
        Rating = rating;
    }
}
