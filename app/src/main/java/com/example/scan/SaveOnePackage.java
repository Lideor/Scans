package com.example.scan;

import android.location.Location;
import android.location.LocationManager;

import java.util.Locale;

public class SaveOnePackage {
    private String lat;
    private String lon;
    private String date;
    private String provider;

    SaveOnePackage(Location location){

        lat = String.format(Locale.ENGLISH,"%1$.4f",location.getLatitude());
        lon = String.format(Locale.ENGLISH,"%1$.4f",location.getLongitude());
        date = String.format(String.format("%1$tF %1$tT",location.getTime()));
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) provider = "GPS";
        else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) provider = "NETWORK";
        else provider = "OTHER";
    }
}
