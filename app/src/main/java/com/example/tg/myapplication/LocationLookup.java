package com.example.tg.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/**
 * Created by TG on 2018-04-02.
 */

public class LocationLookup {
    String TAG_JSON="webnautes";

    double distance;
    public String lookup(Location location, JSONArray jsonArray){
        try {
            Location locationA = new Location("point A");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject ddObject = jsonArray.getJSONObject(i);
                String lat = ddObject.getString("latitude");
                String lon = ddObject.getString("longitude");
                locationA.setLatitude(Double.parseDouble(lat));
                locationA.setLongitude(Double.parseDouble(lon));
                distance = locationA.distanceTo(location);
                if(distance <= 2.0005 && ddObject.getString("element_code").equals("5")){
                    String priority = ddObject.getString("element_priority");
                    return priority;
                }
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }
}
