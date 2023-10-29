package com.itbd.examineradmin.DataMoldes;

import java.io.Serializable;

public class TeacherDataModel implements Serializable {
    private  String fullName, position, email, phone, course, uId;

    public TeacherDataModel() {
        // Default Empty Constructor
    }

    public TeacherDataModel(String fullName, String position, String email, String phone, String course, String uId) {
        this.fullName = fullName;
        this.position = position;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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
