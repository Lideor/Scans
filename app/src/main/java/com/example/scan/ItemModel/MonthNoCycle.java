package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class MonthNoCycle {
    @JsonProperty("number")
    private int number;
    @JsonProperty("days")
    private List<DayNoCycle> days = new ArrayList<DayNoCycle>();

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public DayNoCycle getDay(int day) {

        DayNoCycle req = null;

        for (DayNoCycle i : days) {
            if (i.getNumber() == day) {
                req = i;
            }
        }


        return req;
    }

    MonthNoCycle(NoCycleEvent event){
        number=event.getTime().getStart().getMonth();
        days.add(new DayNoCycle(event));
    }

    public int addEvent(NoCycleEvent event) {

        if(days.get(0).getNumber()>event.getTime().getStart().getDay()){
            days.add(days.indexOf(0),new DayNoCycle(event));
            return 1;
        }
        for (DayNoCycle i : days) {
            if (i.getNumber() == event.getTime().getStart().getDay()) {
                i.addEvent(event);
                return 1;
            }
            if(i.getNumber() < event.getTime().getStart().getDay()){
                days.add(days.indexOf(i),new DayNoCycle(event));
                return 1;
            }
        }
        days.add(new DayNoCycle(event));
        return 1;

    }

    public NoCycleEvent getEvent(int id){
        for(DayNoCycle i:days){
            NoCycleEvent ans = i.getEvent(id);
            if(ans!=null) return ans;
        }
        return null;
    }
}
