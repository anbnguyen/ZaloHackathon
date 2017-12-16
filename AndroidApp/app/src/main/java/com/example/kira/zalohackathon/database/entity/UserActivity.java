package com.example.kira.zalohackathon.database.entity;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by KIRA on 16/12/2017.
 */

public class UserActivity extends RealmObject {
    private int type;
    private Double hrmeanPop;
    private Double hrMeanUser;
    private int counter;
    public UserActivity(){};
    public UserActivity(int type, int counter, Double hrmeanPop, Double hrMeanUser) {
        this.type = type;
        this.counter = counter;
        this.hrmeanPop = hrmeanPop;
        this.hrMeanUser = hrMeanUser;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Double getHrmeanPop() {
        return hrmeanPop;
    }

    public void setHrmeanPop(Double hrmeanPop) {
        this.hrmeanPop = hrmeanPop;
    }

    public Double getHrMeanUser() {
        return hrMeanUser;
    }

    public void setHrMeanUser(Double hrMeanUser) {
        this.hrMeanUser = hrMeanUser;
    }
}
