package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimeRange{


    @JsonProperty("start")
    private Time start;
    @JsonProperty("end")
    private Time end;


    public TimeRange(Time start, Time end){
        this.start=start;
        this.end=end;
    }

    public Time getStart() {
        return start;
    }

    public Time getEnd() {
        return end;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public void setEnd(Time end) {
        this.end = end;
    }
}
