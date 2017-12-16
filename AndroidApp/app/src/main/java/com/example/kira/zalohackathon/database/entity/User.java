package com.example.kira.zalohackathon.database.entity;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by KIRA on 16/12/2017.
 */

public class User extends RealmObject{
    @PrimaryKey
    private String userId;
    private String name;
    private Double weight;
    private Double height;
    private int birthYear;
    private String phone;
    private String emergencyPhone;
    private String emergencyName;
    public User(){};
    public User(String userId,String name, Double weight, Double height, int birthYear, String phone, String emergencyPhone, String emergencyName) {
        this.userId = userId;
        this.name = name;
        this.weight = weight;
        this.height = height;
        this.birthYear = birthYear;
        this.phone = phone;
        this.emergencyPhone = emergencyPhone;
        this.emergencyName = emergencyName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }


    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getEmergencyName() {
        return emergencyName;
    }

    public void setEmergencyName(String emergencyName) {
        this.emergencyName = emergencyName;
    }
}
