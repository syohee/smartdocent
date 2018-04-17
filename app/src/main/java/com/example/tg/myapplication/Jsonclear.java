package com.example.tg.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class Jsonclear {
    private static String TAG = "phpquerytest";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_second_cultural_code = "second_cultural_code";
    private static final String TAG_cultural_photo = "cultural_photo";
    private static final String TAG_latitude ="latitude";
    private static final String TAG_longitude ="longitude";
    public static ArrayList<HashMap<String, String>> mArrayList = new ArrayList<>();
    public ArrayList showResult(String mJsonString){

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);


            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String second_cultural_code = item.getString(TAG_second_cultural_code);
                String cultural_photo = item.getString(TAG_cultural_photo);
                String latitude = item.getString(TAG_latitude);
                String longitude = item.getString(TAG_longitude);
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(TAG_second_cultural_code, second_cultural_code);
                hashMap.put(TAG_cultural_photo, cultural_photo);
                hashMap.put(TAG_latitude, latitude);
                hashMap.put(TAG_longitude,longitude);
                mArrayList.add(hashMap);

            }
            return mArrayList;


        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
            return mArrayList;


        }
    }
}
