package com.mad_devs.fitnesstrackerd.googleMap.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "result_table")
public class DataRoom {
    @PrimaryKey(autoGenerate = true)
    private int id;
    public double distance;
    public long date;
    public String time;
    public String pulse;

    public DataRoom(int id, double distance , long date , String time, String pulse) {
        this.id = id;
        this.distance = distance;
        this.date = date;
        this.time = time;
        this.pulse = pulse;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPulse() {
        return this.pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    @Override
    public String toString() {
        return "DataRoom{" +
                "id=" + id +
                ", distance=" + distance +
                ", date=" + date +
                ", time='" + time + '\'' +
                ", pulse='" + pulse + '\'' +
                '}';
    }
}
