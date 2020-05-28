package com.example.scan.ItemModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Model {

    @JsonProperty("clusters")
    private List<Cluster> clusters = new ArrayList<Cluster>();
    @JsonProperty("city")
    private Point city;
    @JsonProperty("startModel")
    private Time startModel;
    @JsonProperty("model")
    private List<Cycle> model = new ArrayList<Cycle>();
    @JsonProperty("noCycle")
    private List<MonthNoCycle> noCycle = new ArrayList<MonthNoCycle>();

    public Cluster getClusterArray(int id){
        return clusters.get(id);
    }

    public Time getStartModel(){return startModel;}

    public List<String> getNameCluster(){
        List<String> name=new ArrayList<String>();
        for(Cluster i:clusters){
            name.add(i.getName());
        }
        return name;
    }

    public int getIdClusterOnName(String name){
        for(Cluster i:clusters){
            if(i.getName()==name) return i.getClusterId();
        }
        return -1;
    }

    public int addNoCycle(NoCycleEvent event) {
        if(noCycle.get(0).getNumber()>event.getTime().getStart().getMonth()){
            noCycle.add(noCycle.indexOf(0),new MonthNoCycle(event));
            return 1;
        }
        for (MonthNoCycle i : noCycle) {
            if (i.getNumber() == event.getTime().getStart().getMonth()) {
                i.addEvent(event);
                return 1;
            }
            if (i.getNumber() < event.getTime().getStart().getMonth()) {
                noCycle.add(noCycle.indexOf(i), new MonthNoCycle(event));
                return 1;
            }
        }
        noCycle.add(new MonthNoCycle(event));
        return 1;

    }

    public Cluster getCluster(int id) {
        for (Cluster i : clusters) {
            if (i.getClusterId() == id) return i;
        }
        return null;
    }

    public DayNoCycle getDayNoCycle(Time day) {

        DayNoCycle req = null;

        for (MonthNoCycle i : noCycle) {
            if (i.getNumber() == day.getMonth())
                req = i.getDay(day.getDay());

        }

        return req;

    }

    public DayCycle getDayCycle(Time day) {
        DayCycle req = null;
        Date current = new GregorianCalendar(day.getYear(), day.getMonth(), day.getDay()).getTime();
        Date start = new GregorianCalendar(startModel.getYear(), startModel.getMonth(), startModel.getDay()).getTime();
        int delta = (int) Math.abs(current.getTime() - start.getTime()) / (24 * 60 * 60 * 1000);
        delta = (delta / 7) % 2;
        for (Cycle i : model) {
            if (delta == i.getNumber()) req = i.getDay(day);
        }

        return req;

    }

    public List<Event> getEventInDays(Time day) {
        List<Event> out = new ArrayList<Event>();
        DayCycle cycleEvent = this.getDayCycle(day);
        DayNoCycle noCycleEvent = this.getDayNoCycle(day);
        int i = 0;
        int j = 0;
        int nCE = 0;
        int cE = 0;
        if (noCycleEvent != null) nCE = noCycleEvent.event.size();
        if (cycleEvent != null) cE = cycleEvent.event.size();
        while (!(i >= cE & j >= nCE)) {
            if (j < nCE && (i >= cE || !cycleEvent.event.get(i).Сompare(noCycleEvent.event.get(j)))) {
                if (noCycleEvent.event.get(j).getRang() != 0) out.add(noCycleEvent.event.get(j));
                j++;
            } else {
                if (cycleEvent.event.get(i).getRang() != 0) out.add(cycleEvent.event.get(i));
                i++;
            }


        }
        return out;

    }

    public NoCycleEvent getEventNoCycleOnId(int id){
        for(MonthNoCycle i:noCycle){
            NoCycleEvent ans = i.getEvent(id);
            if(ans!=null) return ans;
        }
        return null;
    }
}
