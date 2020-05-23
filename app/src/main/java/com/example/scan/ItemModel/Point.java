package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Point {

    @JsonProperty("lat")
    private double lat;
    @JsonProperty("lon")
    private double lon;

    Point(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

}
