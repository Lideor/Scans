package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cluster {

    @JsonProperty("clusterId")
    private int clusterId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("color")
    private String color;

    @JsonProperty("rang")
    private int rang;

    @JsonProperty("address")
    private String address;

    public int getRang() {
        return rang;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public int getClusterId() {
        return clusterId;
    }

    public String getColor() {
        return color;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRang(int rang) {
        this.rang = rang;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
