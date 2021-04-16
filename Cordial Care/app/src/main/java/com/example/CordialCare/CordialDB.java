package com.example.CordialCare;

import android.content.Context;

import androidx.room.Database;
import androidx.room.TypeConverters;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserDetails.class}, version = 1)
@TypeConverters(CordialConverters.class)
public abstract class CordialDB extends RoomDatabase {
    public abstract UserInfoDao userInfoDao();
    private static CordialDB dbInstance;
    public static synchronized CordialDB getInstance(Context context){

        if(dbInstance == null){
            dbInstance = Room
                    .databaseBuilder(context.getApplicationContext(), CordialDB.class, "sathyamoorthy")
                    .build();
        }
        return dbInstance;
    }
}
