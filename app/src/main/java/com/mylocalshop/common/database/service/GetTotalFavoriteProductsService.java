package com.mylocalshop.common.database.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.Nullable;

import com.mylocalshop.R;
import com.mylocalshop.common.database.LocalRoomDatabaseConnectionsManager;
import com.mylocalshop.common.database.ProductAbstractDatabase;
import com.mylocalshop.post.dao.Product;

import java.util.List;

public class GetTotalFavoriteProductsService extends RemoteViewsService {

    private static final String TAG = "GetTotalFavoriteProductsService";

    private ProductAbstractDatabase products_favorite_db;
    private int totalFavProducts = 0 ;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
       // products_favorite_db = LocalRoomDatabaseConnectionsManager.getStaticInstance(getApplicationContext());
        return new GetTotalFavoriteProductsRemoteViewsFactory();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        products_favorite_db = LocalRoomDatabaseConnectionsManager.getStaticInstance(getApplicationContext());
    }

    private class GetTotalFavoriteProductsRemoteViewsFactory implements RemoteViewsFactory {


        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public RemoteViews getViewAt(int position) {

            // Create a remote view with the total fav products number
            RemoteViews remoteView = new RemoteViews(getApplicationContext().getPackageName(), android.R.layout.simple_list_item_1);
            remoteView.setTextViewText(android.R.id.text1, getString(R.string.total_fav_label_prefix) + " "
                    + String.valueOf(totalFavProducts) + " " + getString(R.string.total_fav_label_suffix));

            return remoteView;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            totalFavProducts = products_favorite_db.productDao().getAll().size();
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
