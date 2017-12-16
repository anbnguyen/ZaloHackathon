package com.example.kira.zalohackathon.database;

import android.app.Application;

import com.example.kira.zalohackathon.database.entity.HeartRate;
import com.example.kira.zalohackathon.database.entity.User;
import com.example.kira.zalohackathon.database.entity.UserActivity;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by KIRA on 16/12/2017.
 */

public class RealmController {
    private Realm realm;

    public RealmController(Application application) {
        Realm.init(application.getApplicationContext());
        realm = Realm.getDefaultInstance();
    }
    public void update(RealmObject ro){
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(ro);
        realm.commitTransaction();
    }
    public RealmResults<User> getByName(String name){
        return realm.where(User.class).contains("name",name).findAll();
    }
    public RealmResults<HeartRate> getByUserId(String userId){
        return realm.where(HeartRate.class).contains("userId",userId).findAll();
    }
    public RealmResults<UserActivity> getByType(int type){
        return realm.where(UserActivity.class).contains("type", String.valueOf(type)).findAll();
    }
}
