package com.itbd.examnierteacher.DataMoldes;

import java.io.Serializable;

public class TeacherDataModel implements Serializable {
    private  String fullName, email, phone, course, uId;

    public TeacherDataModel() {
        // Default Empty Constructor
    }

    public TeacherDataModel(String fullName, String email, String phone, String course, String uId) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.course = course;
        this.uId = uId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
