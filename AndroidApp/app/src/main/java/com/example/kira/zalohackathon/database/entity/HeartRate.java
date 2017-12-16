package com.example.kira.zalohackathon.database.entity;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by KIRA on 16/12/2017.
 */

public class HeartRate extends RealmObject {
    @PrimaryKey
    private String id;
    private int heartRate;
    private Date timeStamp;
    private User user;
    @Index
    private String userId;
    public HeartRate(){}
    public HeartRate(String id, int heartRate, Date timeStamp, User user, String userId) {
        this.id = id;
        this.heartRate = heartRate;
        this.timeStamp = timeStamp;
        this.user = user;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

