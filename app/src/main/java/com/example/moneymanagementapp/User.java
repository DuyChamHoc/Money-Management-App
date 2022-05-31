package com.example.moneymanagementapp;

import com.google.firebase.database.Exclude;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String fullname,email,phone;
    public String birthday;
    public User(){

    }
    public User(String fullname,String birthday,String email,String phone){
        this.fullname=fullname;
        this.birthday=birthday;
        this.email=email;
        this.phone=phone;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("birthday", birthday);
        result.put("fullname", fullname);
        result.put("phone", phone);
        return result;
    }
}


