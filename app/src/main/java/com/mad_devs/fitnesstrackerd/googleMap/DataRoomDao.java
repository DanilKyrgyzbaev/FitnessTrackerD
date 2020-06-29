package com.mad_devs.fitnesstrackerd.googleMap;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.mad_devs.fitnesstrackerd.googleMap.data.DataRoom;

import java.util.List;

@Dao
public interface DataRoomDao {

    // LiveData - это класс держателей данных, который можно наблюдать в рамках данного жизненного цикла.
    // Всегда хранит / кэширует последнюю версию данных. Уведомляет своих активных наблюдателей, когда
    // данные изменились. Так как мы получаем все содержимое базы данных,
    // мы получаем уведомление, когда содержимое базы данных изменилось.

    @Query ("SELECT * FROM result_table")
    LiveData<List<DataRoom>> getAll();

    @Insert()
    long insert(DataRoom dataRoom);

    @Delete()
    void delete(DataRoom dataRoom);

    @Query("DELETE FROM result_table")
    void deleteAll();
}
