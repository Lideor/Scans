package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class Cycle {
    @JsonProperty("number")
    private int number;
    @JsonProperty("days")
    private List<DayCycle> days = new ArrayList<DayCycle>();

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public DayCycle getDay(Time day){
        DayCycle req=null;
        Calendar c = new GregorianCalendar(day.getYear(),day.getMonth()-1,day.getDay());

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK)-1;

        for (DayCycle i:days){
            if(dayOfWeek==i.getNumber()) req=i;
        }

        return req;
    }

}
