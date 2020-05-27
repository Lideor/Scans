package com.example.scan.ItemAnswerAdd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerAddNoCycle {
    @JsonProperty("req")
    private int req=0;
    @JsonProperty("type")
    private int type=0;
    @JsonProperty("id")
    private int id=0;

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int getReq() {
        return req;
    }
}
