package com.example.kira.zalohackathon;

/**
 * Created by steve on 12/17/2017.
 */

public class DataSingleton {

    private static DataSingleton instance = new DataSingleton();
    private int heartRate = 89;
    private int type = 0;

    public static DataSingleton getInstance() {
        return instance;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
