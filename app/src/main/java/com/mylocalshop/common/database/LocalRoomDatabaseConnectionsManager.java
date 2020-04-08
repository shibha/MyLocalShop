package com.mylocalshop.common.database;

import android.content.Context;
import androidx.room.Room;
import com.mylocalshop.R;

public class LocalRoomDatabaseConnectionsManager {

    private static Context context;

    private static ProductAbstractDatabase dbInstance;

    private LocalRoomDatabaseConnectionsManager(){}

    public static  ProductAbstractDatabase getStaticInstance(Context context){
        LocalRoomDatabaseConnectionsManager.context = context;
        if(dbInstance == null) {
            dbInstance = Room.databaseBuilder(context,
                    ProductAbstractDatabase.class, context.getString(R.string.local_db_name)).build();
        }
        return dbInstance;

    }

}
