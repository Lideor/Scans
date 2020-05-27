package com.example.scan.ItemModel;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {

    private int type;
    @JsonProperty("center")
    private Point center;
    @JsonProperty("address")
    private String address;
    @JsonProperty("time")
    private TimeRange time;
    @JsonProperty("rang")
    private int rang;
    @JsonProperty("name")
    private String name;
    @JsonProperty("eventId")
    private int eventId;

    Event(int type){
        this.type = type;
    }

    Event(String address, TimeRange time, int rang,int type){
        this.address=address;
        this.time=time;
        this.rang=rang;
        this.type = type;
    }

    Event(String address, TimeRange time, int rang,int type,String name){
        this.address=address;
        this.name=name;
        this.time=time;
        this.rang=rang;
        this.type = type;
    }

    Event(Point center, String address, TimeRange time, int rang,int type,String name){
        this.address=address;
        this.center=center;
        this.name=name;
        this.time=time;
        this.rang=rang;
        this.type = type;
    }


//тру если событие больше чем текущие
    public boolean Сompare(Event event){
        return (event.time.getStart().getHours()>time.getStart().getHours()||
                event.time.getStart().getMinutes()==time.getStart().getMinutes()&&
                        event.time.getStart().getMinutes()>time.getStart().getMinutes());
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getRang() {
        return rang;
    }

    public Point getCenter() {
        return center;
    }

    public TimeRange getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    public int getEventId() {
        return eventId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setRang(int rang) {
        this.rang = rang;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public void setTime(TimeRange time) {
        this.time = time;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
