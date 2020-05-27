package com.example.scan.ItemModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Time {


    @JsonProperty("year")
    private int year=0;
    @JsonProperty("month")
    private int month=0;
    @JsonProperty("day")
    private int day=0;

    @JsonProperty("hours")
    private int hours=0;

    @JsonProperty("minutes")
    private int minutes=0;

    public Time(int year,int month,int day){
        this.year=year;
        this.month=month;
        this.day=day;
        this.hours=hours;
        this.minutes=minutes;
    }

    public Time(int year,int month,int day,int hours,int minutes){
        this.year=year;
        this.month=month;
        this.day=day;
        this.hours=hours;
        this.minutes=minutes;
    }

    public Time(int hours,int minutes){
        this.hours=hours;
        this.minutes=minutes;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHours(){
        return hours;
    }

    public int getMinutes(){
        return minutes;
    }


    public String getStringShort(){
        String h;
        String m;
        if(hours<10) h="0"+hours;
        else h=""+hours;
        if(minutes<10) m="0"+minutes;
        else m=""+minutes;
        return h+":"+m;
    }

    public String getStringLong(){
        String h;
        String m;
        String d;
        String month;
        if(day<10) d="0"+this.day;
        else d=""+day;
        if(this.month<10) month="0"+this.month;
        else month=""+this.month;
        if(hours<10) h="0"+hours;
        else h=""+hours;
        if(minutes<10) m="0"+minutes;
        else m=""+minutes;
        return year+"-"+month+"-"+d+" "+h+":"+m+":00";
    }
    public String getStringDate(){
        String h;
        String m;
        String d;
        String month;
        if(day<10) d="0"+this.day;
        else d=""+day;
        if(this.month<10) month="0"+this.month;
        else month=""+this.month;
        if(hours<10) h="0"+hours;
        else h=""+hours;
        if(minutes<10) m="0"+minutes;
        else m=""+minutes;
        return year+"-"+month+"-"+d;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
