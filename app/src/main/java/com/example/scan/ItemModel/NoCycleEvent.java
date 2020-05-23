package com.example.scan.ItemModel;

public class NoCycleEvent {
    private Point center;
    private int rang;
    private TimeRange time;
    private int noCycleEventId;
    private String address;

    NoCycleEvent(Point center, int rang, TimeRange time){
        this.center =center;
        this.rang=rang;
        this.time=time;

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public TimeRange getTime() {
        return time;
    }

    public Point getCenter() {
        return center;
    }

    public int getRang() {
        return rang;
    }

    public int getNoCycleEventId() {
        return noCycleEventId;
    }

    public void setTime(TimeRange time) {
        this.time = time;
    }

    public void setRang(int rang) {
        this.rang = rang;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

}
