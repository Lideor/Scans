package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CycleEvent extends Event{

    @JsonProperty("number")
    private int number;
    @JsonProperty("clusterId")
    private int clusterId;

    public CycleEvent(){
        super(1);
    }
    public CycleEvent(int day, Point center, int clusterId, String address, TimeRange time, int rang){
        super(address,time,rang,1);
        this.number = day;
        this.clusterId=clusterId;

    }

    //get
    public int getClusterId() {
        return clusterId;
    }

    public int getNumber() {
        return number;
    }
    //set
    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public void setNumber(int day) {
        this.number = day;
    }


}
