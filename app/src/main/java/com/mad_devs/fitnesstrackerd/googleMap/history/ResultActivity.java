package com.mad_devs.fitnesstrackerd.googleMap.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.mad_devs.fitnesstrackerd.App;
import com.mad_devs.fitnesstrackerd.R;

public class ResultActivity extends AppCompatActivity implements ResultAdapter.onItemClick {
    private RecyclerView recyclerView;
    private ResultAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        recyclerView = findViewById(R.id.recycler_result);
        adapter = new ResultAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Получите новый или существующий ViewModel из ViewModelProvider.

        // Добавьте наблюдателя в LiveData, возвращенный getAlphabetizedWords.
        // Метод onChanged () запускается, когда наблюдаемые данные изменяются и активность
        // на переднем плане.
        App.dataRoomDao.getAll().observe(this, dataRooms -> {
            adapter.addList(dataRooms);
            Log.e("----------history", dataRooms+"");
        });
    }

    @Override
    public void delete(int pos) {
        App.dataRoomDao.delete(adapter.getList().get(pos));
    }
}