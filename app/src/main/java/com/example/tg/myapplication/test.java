package com.example.tg.myapplication;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;



public class test  {

    private static String TAG = "phpquerytest";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_second_cultural_code = "second_cultural_code";
    private static final String TAG_cultural_photo = "cultural_photo";
    private static final String TAG_latitude ="latitude";
    private static final String TAG_longitude ="longitude";
    public static ArrayList<HashMap<String, String>> mArrayList = new ArrayList<>();
    String mJsonString ;

    test(){
    }

       public String test(String mJsonString){
           try {
                GetData task = new GetData();

                    return task.execute(mJsonString).get();
                }
                catch (Exception e){
                    e.printStackTrace();
                    return "e";
                }
        }


    public class GetData extends AsyncTask<String, Void, String>{

        String errorString = null;


        @Override

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            if (result == null){  }
//
//            else {
//                mJsonString = result;
//                showResult();
//            }
        }
        @Override

        protected String doInBackground(String... params) {
            String serverURL = "http://35.184.38.112/jk_DbConect.php";
            String postParameters = "country=" + "1";

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();



                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();



                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();

                }

                else{
                    inputStream = httpURLConnection.getErrorStream();

                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {
                errorString = e.toString();
                return null;
            }
        }
    }
}