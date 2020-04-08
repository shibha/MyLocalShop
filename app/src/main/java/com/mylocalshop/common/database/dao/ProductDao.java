package com.mylocalshop.common.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.mylocalshop.post.dao.Product;
import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM product")
    List<Product> getAll();

    @Query("SELECT * FROM product")
    LiveData<List<Product>> getAllFav();

    @Insert
    void insert(Product product);

    @Delete
    void delete(Product user);

    @Query("SELECT * FROM product WHERE id = :id")
    Product get(String id);
}