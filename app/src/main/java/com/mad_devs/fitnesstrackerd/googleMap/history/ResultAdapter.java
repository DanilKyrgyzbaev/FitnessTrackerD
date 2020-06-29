package com.mad_devs.fitnesstrackerd.googleMap.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad_devs.fitnesstrackerd.R;
import com.mad_devs.fitnesstrackerd.googleMap.data.DataRoom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private List<DataRoom> mDataRooms = new ArrayList<>(); // Кэшированная копия слов
    private onItemClick onItemClick;

    public ResultAdapter(onItemClick onItemClick){
        this.onItemClick = onItemClick;
    }
    public void addList(List<DataRoom> list) {
        mDataRooms.clear();
        mDataRooms.addAll(list);
        notifyDataSetChanged();
    }
    public List<DataRoom> getList() {
        return mDataRooms;
    }

    @NonNull
    @Override
    public ResultAdapter.ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ResultViewHolder(view, onItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.ResultViewHolder holder, int position) {
        holder.bind(mDataRooms.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataRooms != null)
            return mDataRooms.size();
        else return 0;
    }


    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        private TextView data;
        private TextView distance;
        private TextView time;
        private TextView pulse;
        private Button delete;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd \n HH:mm:ss", Locale.getDefault());


        public ResultViewHolder(@NonNull View itemView, onItemClick onItemClick) {
            super(itemView);
            data = itemView.findViewById(R.id.tvDate);
            distance = itemView.findViewById(R.id.tvDistance);
            time = itemView.findViewById(R.id.tvTime);
            pulse = itemView.findViewById(R.id.tvPulse);
            delete = itemView.findViewById(R.id.btnDelete);
            delete.setOnClickListener(v -> onItemClick.delete(getAdapterPosition()));
        }

        private void bind(DataRoom dataRoom) {
            try {
                distance.setText(cutString(String.valueOf(dataRoom.getDistance())) + "м");
            } catch (Exception e){

            }
            time.setText(dataRoom.getTime());
            pulse.setText(String.valueOf(dataRoom.getPulse()));
            data.setText(sdf.format(new Date(dataRoom.getDate())));
        }
    }

    public static String cutString(String str) {
        return str.substring(0, str.length() - 11);
    }

    public interface onItemClick{
        void delete(int pos);
    }
}
