package com.mylocalshop.common.database.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.mylocalshop.R;
import com.mylocalshop.common.database.LocalRoomDatabaseConnectionsManager;
import com.mylocalshop.common.database.ProductAbstractDatabase;
import com.mylocalshop.post.dao.Product;

public class MarkProductFavoriteService extends IntentService {
    private static final String TAG = "MarkProductFavoriteService";

    private ProductAbstractDatabase products_favorite_db;

    public MarkProductFavoriteService() {
        super("MarkProductFavoriteService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        products_favorite_db  = LocalRoomDatabaseConnectionsManager.getStaticInstance(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onStartCommand for " + this.getClass());
        String id = intent.getStringExtra(getString(R.string.id));
        String name = intent.getStringExtra(getString(R.string.name));
        String address = intent.getStringExtra(getString(R.string.address));
        float price = intent.getFloatExtra(getString(R.string.price), 0);
        boolean favorite = intent.getBooleanExtra(getString(R.string.favorite), false);
        System.out.println(" THE favorite is " + favorite);
        Product currentProduct = new Product(id, name, address, price, favorite);
        if(id == null ) id = "default";
        currentProduct.setId(id);
        if (favorite) {
            products_favorite_db.productDao().insert(currentProduct);
        }else{
            products_favorite_db.productDao().delete(currentProduct);
        }

    }

}
