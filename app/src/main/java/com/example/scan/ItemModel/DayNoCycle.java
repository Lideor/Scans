package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class DayNoCycle {

    @JsonProperty("event")
    public List<NoCycleEvent> event = new ArrayList<NoCycleEvent>();
    @JsonProperty("number")
    private int number;

    public int getNumber() {
        return number;
    }

    DayNoCycle(int number){
        this.number=number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int addEvent(NoCycleEvent newEvent){
        for(NoCycleEvent i: event){
            if(i.Сompare(newEvent) ==false){
                event.add(event.indexOf(i),newEvent);
                return 1;
            }
        }
        event.add(newEvent);
        return 1;
    }

    DayNoCycle(NoCycleEvent newEvent){
        event.add(newEvent);
        number=newEvent.getTime().getStart().getDay();
    }

    public NoCycleEvent getEvent(int id){
        for(NoCycleEvent i:event){
            if(i.getNoCycleEventId()==id) return i;
        }
        return null;
    }

}
