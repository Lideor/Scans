package com.example.scan.ItemAddress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReqAddress {

    @JsonProperty("meta")
    public ResultAddress meta;

    @JsonProperty("result")
    public ResultAddress result;
}
