package com.mylocalshop.search.view.model;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.mylocalshop.common.database.LocalRoomDatabaseConnectionsManager;
import com.mylocalshop.post.dao.Product;
import java.util.List;

public class ProductsViewModel extends AndroidViewModel {

    private final LiveData<List<Product>> favProducts;

    public ProductsViewModel(@NonNull Application application) {
        super(application);

        favProducts = LocalRoomDatabaseConnectionsManager.getStaticInstance(application.getApplicationContext())
                .productDao().getAllFav();
    }

    public LiveData<List<Product>> getFavProducts() {
        return favProducts;
    }
}