package com.mad_devs.fitnesstrackerd;

import android.app.Application;

import androidx.room.Room;

import com.mad_devs.fitnesstrackerd.googleMap.DataRoomDao;
import com.mad_devs.fitnesstrackerd.googleMap.DataRoomDatabase;

public class App extends Application {

    public static DataRoomDatabase dataRoomDatabase;
    public static DataRoomDao dataRoomDao;

    @Override
    public void onCreate() {
        super.onCreate();
        dataRoomDatabase = Room.databaseBuilder(this, DataRoomDatabase.class, "fitness_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        dataRoomDao = dataRoomDatabase.getDataRoomDao();
    }
}
