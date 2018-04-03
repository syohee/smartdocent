package com.example.tg.myapplication;

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
    public int lookup(Location location, String mJsonString){
        try {
            Location locationA = new Location("point A");
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject ddObject = jsonArray.getJSONObject(i);
                String lat = ddObject.getString("latitude");
                String lon = ddObject.getString("longitude");
                locationA.setLatitude(Double.parseDouble(lat));
                locationA.setLongitude(Double.parseDouble(lon));
                distance = locationA.distanceTo(location);
                if(distance <= 10.0005){
                    return i+1;
                }
            }
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 404;
        }
    }
}
