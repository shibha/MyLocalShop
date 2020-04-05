package com.mylocalshop.post.dao;

import java.util.List;

public class Product {

    private String name;
    private String address;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    private float price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    private String imageUri;
    private List<String> tags;

    public Product(String name, String address, String imageUri, float price, List<String> tags){
        this.name = name;
        this.address = address;
        this.imageUri = imageUri;
        this.price = price;
        this.tags = tags;
    }

    public Product(){}

}
