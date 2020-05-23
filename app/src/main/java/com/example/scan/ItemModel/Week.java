package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Week {
    @JsonProperty("day")
    private List<CycleEvent> day = new ArrayList<CycleEvent>();
    @JsonProperty("number")
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
