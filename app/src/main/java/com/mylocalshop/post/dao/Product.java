package com.mylocalshop.post.dao;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Product {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "favorite")
    private boolean favorite;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }


    @ColumnInfo(name = "price")
    private float price;

    public String getName() {
        return name;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
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

    @Ignore
    private String imageUri;
    @Ignore
    private List<String> tags;

    @Ignore
    public Product(String id, String name, String address, String imageUri, float price,
                   List<String> tags, boolean favorite){
        this.id = id;
        this.name = name;
        this.address = address;
        this.imageUri = imageUri;
        this.price = price;
        this.tags = tags;
        this.favorite = favorite;
    }

    @Ignore
    public Product(String name, String address, String imageUri, float price,
                   List<String> tags){
        this.id = id;
        this.name = name;
        this.address = address;
        this.imageUri = imageUri;
        this.price = price;
        this.tags = tags;
        this.favorite = favorite;
    }

    public Product(String id, String name, String address, float price, boolean favorite){
        this.id = id;
        this.name = name;
        this.address = address;
        this.price = price;
        this.favorite = favorite;
    }

    @Ignore
    public Product(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
