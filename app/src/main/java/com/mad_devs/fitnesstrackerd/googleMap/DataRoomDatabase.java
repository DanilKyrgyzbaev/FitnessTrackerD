package com.mad_devs.fitnesstrackerd.googleMap;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mad_devs.fitnesstrackerd.googleMap.data.DataRoom;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {DataRoom.class}, version = 3, exportSchema = false)
abstract public class DataRoomDatabase extends RoomDatabase {
    public abstract DataRoomDao getDataRoomDao();
}
//@Database(entities = {DataRoom.class}, version = 3, exportSchema = false)
//abstract class DataRoomDatabase extends RoomDatabase {
//    abstract DataRoomDao dataRoomDao();
//    private static volatile DataRoomDatabase INSTANCE;
//    private static final int NUMBER_OF_THREADS = 4;
//    static final ExecutorService databaseWriteExecutor =
//            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
//
//    static DataRoomDatabase getDatabase(final Context context) {
//        if (INSTANCE == null) {
//            synchronized (DataRoomDatabase.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = Room.databaseBuilder ( context.getApplicationContext () ,
//                            DataRoomDatabase.class , "word_database" )
//                            .addCallback ( sRoomDatabaseCallback )
//                            .allowMainThreadQueries ()
//                            .fallbackToDestructiveMigration ()
//                            .build ();
//                }
//            }
//        }
//        return INSTANCE;
//    }
//
//    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
//        @Override
//        public void onOpen(@NonNull SupportSQLiteDatabase db) {
//            super.onOpen(db);
//
//            // Если вы хотите сохранить данные через перезапуск приложения,
//            // закомментируйте следующий блок
//            databaseWriteExecutor.execute(() -> {
//                // Заполните базу данных в фоновом режиме.
//                // Если вы хотите начать с большего количества слов, просто добавьте их.
//                DataRoomDao dao = INSTANCE.dataRoomDao();
//                dao.deleteAll();
//
//            });
//        }
//    };
//}
