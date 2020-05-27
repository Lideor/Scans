package com.example.scan.ItemAddress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultAddress {

    @JsonProperty("viewport")
    PointAddress viewport = new PointAddress();

    public PointAddress getViewport() {
        return viewport;
    }
}
