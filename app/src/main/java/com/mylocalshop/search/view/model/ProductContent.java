package com.mylocalshop.search.view.model;

import android.graphics.Bitmap;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mylocalshop.post.dao.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductContent {

    public static final List<ProductItem> PRODUCTS = new ArrayList<>();

    public static final Map<String, ProductItem> PRODUCT_MAP = new HashMap<String, ProductItem>();

    public List<ProductItem> getItems(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReference = database.getReference();
        DatabaseReference ref = dbReference.child("products");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Product newProduct = dataSnapshot.getValue(Product.class);
                if(PRODUCT_MAP.get(prevChildKey) == null) {
                    newProduct.setId(prevChildKey);
                    addItem(convertToViewModel(newProduct, prevChildKey));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}

            private ProductItem convertToViewModel(Product product, String prevProductId){
                ProductItem item = new ProductItem(prevProductId , product.getName() ,
                        String.valueOf(product.getPrice()), product.getImageUri(), product.getAddress(),
                        product.isFavorite());
                return item;

            }
        });

        return PRODUCTS;

    }

    public static void addItem(ProductItem item) {
        PRODUCTS.add(item);
        System.out.println("ADDING TO MAP MALIK WITH ID " + item.id);
        System.out.println("ADDING TO MAP MALIK WITH ID " + item.name);

        PRODUCT_MAP.put(item.id, item);
    }

    public static class ProductItem {
        public String id;
        public final String name;
        public final String price;
        public boolean favorite;

        public void setImgBitMap(Bitmap bitMap) {
            this.imgBitMap = bitMap;
        }

        public final String imgUri;
        public final String address;
        public Bitmap imgBitMap = null;

        public ProductItem(String id, String name, String price, String imgUri, String address, boolean favorite) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.imgUri = imgUri;
            this.address = address;
            this.favorite = favorite;
        }



        @Override
        public String toString() {
            return name + imgUri;
        }
    }
}
