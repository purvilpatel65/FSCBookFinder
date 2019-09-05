package com.example.me.fscbookfinder.model;

import java.util.List;

public class Item {

    private String title;
    private String author;
    private String ISBN;
    private String publisher;
    private String Edition;
    private double price;
    private String imgCoverUrl;
    private String img1Url;
    private String img2Url;
    private String img3Url;
    private String img4Url;
    private String currentUserUID;


    public Item()
    {
        //Empty Constructor

    }

    public Item(String title, String author, String ISBN, String publisher, String edition, double price, String imgCoverUrl, String img1Url, String img2Url, String img3Url, String img4Url, String currentuid) {
        if (publisher.trim().equals("")) {
            publisher = "";
        }
        if (edition.trim().equals("")) {
            edition = "";
        }

        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.publisher = publisher;
        Edition = edition;
        this.price = price;
        this.imgCoverUrl = imgCoverUrl;
        this.img1Url = img1Url;
        this.img2Url = img2Url;
        this.img3Url = img3Url;
        this.img4Url = img4Url;
        this.currentUserUID = currentuid;

    }


    public String getImgCoverUrl() {
        return imgCoverUrl;
    }

    public void setImgCoverUrl(String imgCoverUrl) {
        this.imgCoverUrl = imgCoverUrl;
    }

    public String getImg1Url() {
        return img1Url;
    }

    public void setImg1Url(String img1Url) {
        this.img1Url = img1Url;
    }

    public String getImg2Url() {
        return img2Url;
    }

    public void setImg2Url(String img2Url) {
        this.img2Url = img2Url;
    }

    public String getImg3Url() {
        return img3Url;
    }

    public void setImg3Url(String img3Url) {
        this.img3Url = img3Url;
    }

    public String getImg4Url() {
        return img4Url;
    }

    public void setImg4Url(String img4Url) {
        this.img4Url = img4Url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getEdition() {
        return Edition;
    }

    public void setEdition(String edition) {
        this.Edition = edition;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) { this.price = price; }

    public String getCurrentUserUID() {
        return currentUserUID;
    }

    public void setCurrentUserUID(String currentUserUID) {
        this.currentUserUID = currentUserUID;
    }

}
