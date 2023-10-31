package com.itbd.examineradmin.DataMoldes;

import java.io.Serializable;

public class AdminDataModel implements Serializable {
    String name, email, phone, userId;

    public AdminDataModel() {
    }

    public AdminDataModel(String name, String email, String phone, String userId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
