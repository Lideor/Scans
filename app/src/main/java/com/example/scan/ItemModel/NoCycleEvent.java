package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NoCycleEvent extends Event{



    @JsonProperty("color")
    private String color;

    public NoCycleEvent(Point center, int rang, TimeRange time,String address,String name,String color){
        super(center,address,time,rang,0,name);
        this.color = color;

    }

    public NoCycleEvent(int rang, TimeRange time,String address,String name,String color){
        super(address,time,rang,0,name);
        this.color = color;

    }

    public NoCycleEvent(int rang, TimeRange time,String address){
        super(address,time,rang,0);

    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public int getNoCycleEventId() {
        return getEventId();
    }

}
