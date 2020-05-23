package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CycleEvent {

    @JsonProperty("lat")
    private int number;
    @JsonProperty("lat")
    private Point center;
    @JsonProperty("lat")
    private int clusterId;
    @JsonProperty("lat")
    private String address;
    @JsonProperty("lat")
    private TimeRange time;
    @JsonProperty("lat")
    private int rang;

    CycleEvent(int day, Point center, int clusterId, String address, TimeRange time, int rang){
        this.number = day;
        this.address=address;
        this.center=center;
        this.clusterId=clusterId;
        this.address=address;
        this.time=time;
        this.rang=rang;
    }

    //get
    public int getClusterId() {
        return clusterId;
    }

    public int getNumber() {
        return number;
    }

    public int getRang() {
        return rang;
    }

    public Point getCenter() {
        return center;
    }

    public String getAddress() {
        return address;
    }

    public TimeRange getTime() {
        return time;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //set
    public void setCenter(Point center) {
        this.center = center;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public void setNumber(int day) {
        this.number = day;
    }

    public void setRang(int rang) {
        this.rang = rang;
    }

    public void setTime(TimeRange time) {
        this.time = time;
    }
}
