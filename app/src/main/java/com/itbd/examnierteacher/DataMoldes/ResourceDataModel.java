package com.itbd.examnierteacher.DataMoldes;

public class ResourceDataModel {

    String resource, date, time, user, course, key;

    public ResourceDataModel() {
        // Default Empty Constructor
    }

    public ResourceDataModel(String resource, String date, String time, String user, String course, String key) {
        this.resource = resource;
        this.date = date;
        this.time = time;
        this.user = user;
        this.course = course;
        this.key = key;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
