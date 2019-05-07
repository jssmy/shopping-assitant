package com.google.cloud.android.speech.entities;

public class ItemCart {
    private     int id;
    private     String product_name;
    private     int quantity;
    private     float price;
    private     int discount;
    private     boolean confirmed;
    private     String url_img;



    public ItemCart(int id, String product_name, int quantity, float price, int discount, boolean confirmed) {
        this.id = id;
        this.product_name = product_name;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.confirmed = confirmed;
    }

    public ItemCart(int id, String product_name, int quantity, float price, int discount, boolean confirmed, String url_img) {
        this.id = id;
        this.product_name = product_name;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.confirmed = confirmed;
        this.url_img = url_img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public float currentPrice()
    {
        return  this.round(this.price - this.price*((float)(this.discount/100.0)));
    }

    private  float round(float val)
    {
        return (float)(Math.round(val*100.0)/100.0);
    }

    public String getUrl_img() {
        return url_img;
    }

    public void setUrl_img(String url_img) {
        this.url_img = url_img;
    }
}
