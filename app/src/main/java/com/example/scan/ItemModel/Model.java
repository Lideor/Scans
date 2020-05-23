package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Model {

    @JsonProperty("model")
    private List<Cycle> model = new ArrayList<Cycle>();
    @JsonProperty("no_cycle")
    private List<NoCycleEvent> no_cycle = new ArrayList<NoCycleEvent>();
}
