package com.mylocalshop.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import com.mylocalshop.R;
import com.mylocalshop.common.database.service.GetTotalFavoriteProductsService;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TotalProductsOnShopWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            RemoteViews remoteViews = createAppWidgetRemoteViews(context, id);
            appWidgetManager.updateAppWidget(id, remoteViews);
        }
    }

    private RemoteViews createAppWidgetRemoteViews(Context context, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.total_products_widget_layout);

        Intent intent = new Intent(context, GetTotalFavoriteProductsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        remoteViews.setRemoteAdapter(appWidgetId, R.id.totalFavProductsList, intent);

        remoteViews.setEmptyView(R.id.totalFavProductsList, R.id.total_products_number);

        Log.i(TAG,"Returning Remote Views !!" );

        return remoteViews;
    }
//
////    @Override
////    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
////        ComponentName thisWidget = new ComponentName(context,
////                TotalProductsOnShopWidgetProvider.class);
////        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
////        for (int widgetId : allWidgetIds) {
////            remoteViews = new RemoteViews(context.getPackageName(),
////                    R.layout.total_products_widget_layout);
////
////
////            //Start a service to get the total favorites from local room database
////            Intent getTotalFavProductsServiceIntent = new Intent(context, GetTotalFavoriteProductsService.class);
////            IntentFilter recieveTotalFavProductsIntentFilter = new IntentFilter(context.getString(R.string.is_product_fav_broadcast_action));
////            context.startService(getTotalFavProductsServiceIntent);
////            context.getApplicationContext().registerReceiver(this, recieveTotalFavProductsIntentFilter);
////
////            Intent providerIntent = new Intent(context, TotalProductsOnShopWidgetProvider.class);
////            providerIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
////            providerIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
////            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
////                    0, providerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
////            remoteViews.setOnClickPendingIntent(R.id.total_products_number, pendingIntent);
////            appWidgetManager.updateAppWidget(widgetId, remoteViews);
////        }
////    }
////
////    @Override
////    public void onReceive(Context context, Intent intent) {
////        super.onReceive(context, intent);
////        if(remoteViews == null){
////            return;
////        }
//        int totalFavProducts = intent.getIntExtra(context.getString(R.string.total_fav), 0);
//        Log.info("I MISS U !!!!!!! Widget data " + totalFavProducts);
//        remoteViews.setTextViewText(R.id.total_products_number, String.valueOf(totalFavProducts));
//        context.getApplicationContext().unregisterReceiver(this);
////
////    }




}
