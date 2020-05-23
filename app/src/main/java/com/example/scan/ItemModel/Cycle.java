package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Cycle {
    @JsonProperty("number")
    private int number;
    @JsonProperty("week")
    private List<Week> week = new ArrayList<Week>();

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
