package com.example.kira.zalohackathon.database.entity;

import io.realm.RealmObject;

/**
 * Created by steve on 12/17/2017.
 */

public class TempAct extends RealmObject {
    private int type;
    private long id;
    public TempAct(){}
    public TempAct(long id,int type){
        this.id = System.currentTimeMillis()/1000;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
