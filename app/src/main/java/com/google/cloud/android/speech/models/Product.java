package com.google.cloud.android.speech.models;

public class Product {

    private int ID;
    private String name;
    private  float price;
    private  int ranking;
    private  String description;
    private String url_img;
    private String tags;

    public Product() {
    }

    public Product(int ID, String name, float price, int ranking, String description, String url_img, String tags) {
        this.ID = ID;
        this.name = name;
        this.price = price;
        this.ranking = ranking;
        this.description = description;
        this.url_img = url_img;
        this.tags = tags;
    }

    public Product(String name, float price, int ranking, String description, String url_img) {
        this.name = name;
        this.price = price;
        this.ranking = ranking;
        this.description = description;
        this.url_img = url_img;
    }

    public Product(int ID, String name, float price, int ranking, String description, String url_img) {
        this.ID = ID;
        this.name = name;
        this.price = price;
        this.ranking = ranking;
        this.description = description;
        this.url_img = url_img;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl_img() {
        return url_img;
    }

    public void setUrl_img(String url_img) {
        this.url_img = url_img;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
