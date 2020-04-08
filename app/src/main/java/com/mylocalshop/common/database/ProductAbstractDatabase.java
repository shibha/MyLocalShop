package com.mylocalshop.common.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mylocalshop.common.database.dao.ProductDao;
import com.mylocalshop.post.dao.Product;

@Database(entities = {Product.class}, version = 1, exportSchema=false)
public abstract class ProductAbstractDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
}