package com.mad_devs.fitnesstrackerd.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;



public class InformationModel extends RealmObject {


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey
    private int id;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    private String distance;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;

    public String getCurrentTimeDate() {
        return currentTimeDate;
    }

    public void setCurrentTimeDate(String currentTimeDate) {
        this.currentTimeDate = currentTimeDate;
    }

    private String currentTimeDate;

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    private String rate;
}
