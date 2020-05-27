package com.example.scan.ItemAddress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PointAddress {


    @JsonProperty("TopLat")
    private double TopLat=0;
    @JsonProperty("TopLob")
    private double TopLob=0;

    public double getTopLat() {
        return TopLat;
    }

    public double getTopLob() {
        return TopLob;
    }
}
