package com.example.kira.zalohackathon.database.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by steve on 12/17/2017.
 */

public class InvalidWarning extends RealmObject {
    @PrimaryKey
    private String userid;
    private int type;
    private int heartrate;

    public InvalidWarning(){}
    public InvalidWarning(String userid, int type, int heartrate){
        this.userid = userid;
        this.type = type;
        this.heartrate = heartrate;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getHeartrate() {
        return heartrate;
    }

    public void setHeartrate(int heartrate) {
        this.heartrate = heartrate;
    }
}
