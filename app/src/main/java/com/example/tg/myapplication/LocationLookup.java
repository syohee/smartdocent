package com.example.tg.myapplication;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONObject;

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
                else if(distance <= 2.0005 && ddObject.getString("element_code").equals("3")){
                     return "AR";
                }
                else if(distance <= 2.0005 && ddObject.getString("element_code").equals("2")){
                    return "QR";
                }
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }
}
