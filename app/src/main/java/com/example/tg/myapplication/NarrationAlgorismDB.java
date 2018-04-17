package com.example.tg.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by seonghee on 2018-04-09.
 */

public class NarrationAlgorismDB {
    private static String TAG = "NarrationAlgorism";
    private static final String TAG_JSON = "smartdocent";
//    private String urlIP = "http://35.184.38.112/";       // 서버
    private String urlIP = "http://172.25.1.62/";         // 로컬
//private String urlIP = "http://127.0.0.1/";         // 로컬
    String resultString;
    public static ArrayList<HashMap<String, String>> mExplPointList;
    public static ArrayList<HashMap<String, String>> mArrayList;
    public static ArrayList<HashMap<String, String>> mFileNameList;
    public static ArrayList<HashMap<String, String>> mFileNameNowList;
    public static ArrayList<HashMap<String, String>> mEndPointList;
    public static ArrayList<HashMap<String, String>> mSectionList;

    // element_detail_code 로 파일 코드, 파일 명 가져오기
    class GetSection extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected String doInBackground(String... params) {
            String ete = params[0];
            String nowPoint = params[1];
            String serverURL = urlIP + "mGetSection.php";
            String postParameters = "ete=" + ete + "&element_detail_code=" + nowPoint;

            Log.d(TAG, "getFileNameNow/doInBackground: 예상 도착시간, 현재 해설 포인트로 구간해설파일을 찾습니다. " +
                    "예상 소요 시간 : " + ete + ", 현재 해설 포인트 : " + nowPoint + "입니다.");
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
//                Log.d(TAG, "getSection/doInBackground: 서버로 부터 응답을 받았습니다. response code : " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }else{
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
                Log.d(TAG, "getSection/doInBackground: 예외가 발생했습니다.");
                errorString = e.toString();
                Log.i(TAG, "doInBackground: " + errorString);
                return null;
            }
        }
    }

    public void getSectionResult(){
        try {
            JSONObject jsonObject = new JSONObject(resultString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String data_file_name = item.getString("data_file_name");
                String data_file_code = item.getString("data_file_code");

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("data_file_name", data_file_name);
                hashMap.put("data_file_code", data_file_code);

                mSectionList.add(hashMap);
            }
            MapsActivity.section_check = !mSectionList.get(0).get("data_file_code").equals("-1");
            if(MapsActivity.section_check){    // 해설 파일 있음
                MapsActivity.newFileName = mSectionList.get(0).get("data_file_name");
                MapsActivity.newFileCode = mSectionList.get(0).get("data_file_code");
                Log.d(TAG, "getSectionResult: 구간해설 파일이 있습니다. : " + MapsActivity.newFileName);
//                MapsActivity.fileChange = "yes";
                MapsActivity.playExpl();
            }else{  // 파일 없음
                Log.d(TAG, "getSectionResult: 구간해설 파일이 없습니다.");
                MapsActivity.section_check = false;
            }
        }catch (JSONException e){
            Log.d(TAG, "getElementResult: ", e);
        }
    }

    // element_detail_code 로 파일 코드, 파일 명 가져오기
    class GetEndPoint extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override
        protected String doInBackground(String... params) {
            String data_file_code = params[0];
            String playTime = params[1];
            String serverURL = urlIP + "mGetEndPoint.php";
            String postParameters = "data_file_code=" + data_file_code + "&playTime=" + playTime;

            Log.d(TAG, "getEndPoint/doInBackground: 파일코드, 현재 시간으로 종료지점을 찾습니다. " +
                    "파일코드 : " + data_file_code + ", 현재 재생 시간 : " + playTime + "입니다.");
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
                Log.d(TAG, "getEndPoint/doInBackground: 서버로 부터 응답을 받았습니다. response code : " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }else{
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
                Log.d(TAG, "getEndPoint/doInBackground: 예외가 발생했습니다.");
                errorString = e.toString();
                Log.i(TAG, "doInBackground: " + errorString);
                return null;
            }
        }
    }

    public void getEndPointResult(){
        try {
            JSONObject jsonObject = new JSONObject(resultString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String end_point = item.getString("end_point");

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("end_point", end_point);

                mEndPointList.add(hashMap);
            }
            String end = mEndPointList.get(0).get("end_point");
            if(!end.equals("null"))
                MapsActivity.end_point = Double.parseDouble(end);
            Log.d(TAG, "getEndPointResult: 종료 포인트("+ MapsActivity.end_point +") - 실행시간(" + MapsActivity.playTime +")");
//            Log.d(TAG, "getEndPointResult: 실행 시간 playTime : " + MapsActivity.playTime);
            MapsActivity.setTime = (int)(MapsActivity.end_point - Double.parseDouble(MapsActivity.playTime))*1000;
            Log.d(TAG, "getEndPointResult: 타임 세팅 : " + MapsActivity.setTime);
            MapsActivity.end_point_sleep();
        }catch (JSONException e){
            Log.d(TAG, "getEndPointResult: ", e);
        }
    }

    // element_detail_code 로 파일 코드, 파일 명 가져오기
    class GetFileNameNow extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected String doInBackground(String... params) {
            String element_detail_code = params[0];
            String serverURL = urlIP + "mGetFileNameNow.php";
            String postParameters = "element_detail_code=" + element_detail_code;
//            Log.d(TAG, "getFileNameNow/doInBackground: 새로운 해설 포인트의 해설파일코드, 파일명을 찾습니다. " +
//                    "세부 엘리먼트 코드 : " + element_detail_code + "입니다.");
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
//                Log.d(TAG, "getFileNameNow/doInBackground: 서버로 부터 응답을 받았습니다. response code : " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }else{
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
                Log.d(TAG, "getFileNameNow/doInBackground: 예외가 발생했습니다.");
                errorString = e.toString();
                Log.i(TAG, "doInBackground: " + errorString);
                return null;
            }
        }
    }

    public void getFileNameNowResult(){
        try {
            JSONObject jsonObject = new JSONObject(resultString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String data_file_name = item.getString("data_file_name");
                String data_file_code = item.getString("data_file_code");

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("data_file_name", data_file_name);
                hashMap.put("data_file_code", data_file_code);

                mFileNameNowList.add(hashMap);
            }
            MapsActivity.newFileName = mFileNameNowList.get(0).get("data_file_name");
            MapsActivity.newFileCode = mFileNameNowList.get(0).get("data_file_code");
            Log.d(TAG, "getFileNameNowResult: 새로 가져온 파일 명 : " + MapsActivity.newFileName);
            Log.d(TAG, "getFileNameNowResult: 새로 가져온 파일 코드 : " + MapsActivity.newFileCode);
        }catch (JSONException e){
            Log.d(TAG, "getFileNameNowResult: ", e);
        }
    }

    // 문화재 코드, 엘리먼트 코드로 파일 코드, 파일 명 가져오기
    class GetFileName extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected String doInBackground(String... params) {
            String cultural_code = params[0];
            String element_code = params[1];
            String serverURL = urlIP + "mGetFileName.php";
            String postParameters = "cultural_code=" + cultural_code + "&element_code=" + element_code;

            Log.d(TAG, "getFileName/doInBackground: 문화재코드와 요소코드로 해설파일명을 찾습니다. " +
                    "문화재코드 : " + cultural_code + ", 요소코드 : " + element_code + "입니다.");
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
                Log.d(TAG, "getFileName/doInBackground: 서버로 부터 응답을 받았습니다. response code : " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }else{
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
                Log.d(TAG, "getFileName/doInBackground: 예외가 발생했습니다.");
                errorString = e.toString();
                Log.i(TAG, "doInBackground: " + errorString);
                return null;
            }
        }
    }

    public void getFileNameResult(){
        try {
            JSONObject jsonObject = new JSONObject(resultString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String data_file_name = item.getString("data_file_name");
                String data_file_code = item.getString("data_file_code");

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("data_file_name", data_file_name);
                hashMap.put("data_file_code", data_file_code);

                mFileNameList.add(hashMap);
            }
            MapsActivity.newFileName = mFileNameList.get(0).get("data_file_name");
            MapsActivity.newFileCode = mFileNameList.get(0).get("data_file_code");
            MapsActivity.playExpl();
        }catch (JSONException e){
            Log.d(TAG, "getFileNameResult: ", e);
        }
    }

    // 해설 포인트 가져오기
    class GetExplPoint extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected String doInBackground(String... strings) {
            String serverURL = urlIP + "mGetExplPoint.php";
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();
//                Log.d(TAG, "getExplPoint/doInBackground: 서버로 부터 응답을 받았습니다. response code : " + responseStatusCode);
//                t(m.mContext, "getExplPoint/doInBackground: 서버로 부터 응답을 받았습니다. response code : " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }else{
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
                Log.d(TAG, "getExplPoint/doInBackground: 해설포인트를 가져오던 중 예외가 발생했습니다.");
                errorString = e.toString();
                Log.i(TAG, "doInBackground: " + errorString);
                return null;
            }
        }
    }

    public void getExplPointResult(){
        try {
            mExplPointList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(resultString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String element_detail_code = item.getString("element_detail_code");
                String element_priority = item.getString("element_priority");
                String element_code = item.getString("element_code");
                String latitude = item.getString("latitude");
                String longitude = item.getString("longitude");

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("element_detail_code", element_detail_code);
                hashMap.put("element_priority", element_priority);
                hashMap.put("element_code", element_code);
                hashMap.put("latitude", latitude);
                hashMap.put("longitude", longitude);

                mExplPointList.add(hashMap);
            }
//            new MapsActivity().onAddMarker();
//            int lastIndex = mExplPointList.size()-1;
//            MapsActivity.maxPriority = Integer.parseInt(mExplPointList.get(lastIndex).get("element_priority"));
        }catch (JSONException e){
            Log.d(TAG, "getExplPointResult: ", e);
        }
    }

    // 사용자 현재 위치가 해설포인트 반경 안에 들어갔는지 체크
    class GetElement extends AsyncTask<String, Void, String> {
        String errorString = null;
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            Log.d(TAG, "getElement/onPostExecute: 서버로부터 받은 정보입니다. POST response : " + result);
//            t(m.mContext, "getElement/onPostExecute: 서버로부터 받은 정보입니다. POST response : " + result);
            if(result != null){
                resultString = result;
                mArrayList = new ArrayList<>();
                getElementResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String userLat = params[0];
            String userLon = params[1];
            String serverURL = urlIP + "mGetElement.php";
            String postParameters = "userLat=" + userLat + "&userLon=" + userLon;

//            Log.d(TAG, "getElement/doInBackground: 1. 사용자 위치로 해설 포인트를 찾겠습니다. " +
//                    "현재 사용자 위치는 위도 : " + userLat + ", 경도 : " + userLon + "입니다.");
//            t(m.mContext, "getElement/doInBackground: 1. 사용자 위치로 해설 포인트를 찾겠습니다. " +
//                    "현재 사용자 위치는 위도 : " + userLat + ", 경도 : " + userLon + "입니다.");
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
//                Log.d(TAG, "getElement/doInBackground: 2. 사용자 위치정보를 서버로 보냅니다.");
//                t(m.mContext, "getElement/doInBackground: 2. 사용자 위치정보를 서버로 보냅니다.");


                int responseStatusCode = httpURLConnection.getResponseCode();
//                Log.d(TAG, "getElement/doInBackground: 3. 서버로 부터 응답을 받았습니다. response code : " + responseStatusCode);
//                t(m.mContext, "getElement/doInBackground: 3. 서버로 부터 응답을 받았습니다. response code : " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }else{
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
                Log.d(TAG, "getElement/doInBackground: 예외가 발생했습니다.");
                errorString = e.toString();
                Log.i(TAG, "doInBackground: " + errorString);
                return null;
            }
        }
    }

    public void getElementResult(){
        try {
            JSONObject jsonObject = new JSONObject(resultString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String element_detail_code = item.getString("element_detail_code");
                String element_priority = item.getString("element_priority");
                String element_code = item.getString("element_code");

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("element_detail_code", element_detail_code);
                hashMap.put("element_priority", element_priority);
                hashMap.put("element_code", element_code);

                mArrayList.add(hashMap);
            }
            MapsActivity.point = mArrayList.get(0).get("element_detail_code");
            String pri = mArrayList.get(0).get("element_priority");
            MapsActivity.nowPointCode = mArrayList.get(0).get("element_code");
            if(!pri.equals("null") && !pri.equals("-1")) {   // 순서 없으면 99 -> ar이나 qr등
                MapsActivity.priority = Integer.valueOf(pri);
            }else if(pri.equals("-1")){
                // 데이터 없으면 -1(해설포인트 밖) -> 순서값 변경X -> 이전 해설포인트 순서고정
            }else{
                MapsActivity.priority = 99;
            }
            if(!MapsActivity.point.equals("-1")){
//                Log.d(TAG, "getElementResult: 해설포인트 영역 안입니다. 해설포인트 코드 : " + m.point);
//                t(m.mContext, "해설 포인트 영역 안입니다. 해설포인트 코드 : " + m.point);
            }else{
//                Log.d(TAG, "getElementResult: 해설포인트 영역 밖입니다.");
//                t(m.mContext, "해설 포인트 영역 밖입니다.");
            }
        }catch (JSONException e){
            Log.d(TAG, "getElementResult: ", e);
        }
    }
}
