package com.example.tg.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class StampActivity extends AppCompatActivity  {

    private static String TAG = "phptest_StampActivity";
    private static final String TAG_JSON = "result";
    private static final String TAG_NAME = "name";
    private static final String TAG_CUL_CODE = "cul_code";
    private static final String TAG_STAMP_STATE = "stamp_state";

    //하드코딩의 재료
    TextView tx1,tx2,tx3,tx4;
    ImageView ix1,ix2,ix3,ix4;


    Intent intent;
    String user_id;
    int lang_code;
    String qrExplCont;
    Spinner spinner;

    ArrayList<HashMap<String, String>> arrayList;
    //    GridView gridView;
    static String jsonString;
    String[] gridViewString;
    int[]gridViewImageId;

    ArrayList<String> areaList;

    // TextView qrExplText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mystamp);
        intent = getIntent();
        user_id = intent.getStringExtra("id");
        lang_code = intent.getIntExtra("lang_code", lang_code);
        qrExplCont = intent.getStringExtra("qrExpl");

        /*qrExplText = (TextView)findViewById(R.id.qrExpl);
        qrExplText.setText(qrExplCont);*/

        Log.d(TAG, "id -> " + user_id + ", lang_code -> " + lang_code);
        Log.d(TAG, "qrExpl -> " + qrExplCont);

        areaList = new ArrayList<String>();
        areaList.add("강원도");
        areaList.add("경기도");
        areaList.add("광주광역시");
        areaList.add("경상남도");
        areaList.add("경상북도");
        areaList.add("대구광역시");
        areaList.add("대전광역시");
        areaList.add("서울특별시");
        areaList.add("울산광역시");
        areaList.add("인천광역시");
        areaList.add("전라남도");
        areaList.add("전라북도");
        areaList.add("제주도");
        areaList.add("충청남도");
        areaList.add("충청북도");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, areaList);
        spinner = (Spinner)this.findViewById(R.id.stampArea);
        // spinner.setPrompt("지역");
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)  {
                // Toast.makeText(this, areaList.get(position), Toast.LENGTH_LONG).show();
                Log.d(TAG, "position -> " + position);
                Log.d(TAG, "id -> "+ user_id);

                StampList stampList = new StampList();
                stampList.execute(areaList.get(position), user_id, String.valueOf(lang_code));

                // showStamp();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // 문화재 페이지 버튼
        ImageButton cultureBtn = (ImageButton)findViewById(R.id.cultureBtn);
        cultureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent cultureIntent = new Intent(getApplicationContext(), MainActivity.class);
                cultureIntent.putExtra("lang_code", lang_code);
                startActivity(cultureIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        // 뒤로가기 버튼
        ImageButton backBtn = (ImageButton)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                /*Intent backIntent = new Intent(getApplicationContext(), MyPageActivity.class);
                startActivity(backIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);*/
                Log.d(TAG, "클릭됨");
                finish();
            }
        });
    }

    public class StampList extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = progressDialog.show(StampActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            Log.d(TAG, "Post response -> " + s);

            if(s == null) {

            } else {
                jsonString = s;
                showStamp();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String position = strings[0];
            String user_id = strings[1];
            String langCode = strings[2];

            Log.d(TAG, "position -> " + position + ", user_id -> " + user_id + ", lang_code -> " + langCode);

            String serverUrl = "http://35.184.38.112/stampList.php";
            String post = "position=" + position + "&user_id=" + user_id + "&lang_code=" + langCode;

            try {
                URL url = new URL(serverUrl);

                java.net.HttpURLConnection httpURLConnection = (java.net.HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(post.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "Post response code -> " + responseStatusCode);

                InputStream inputStream;

                if (responseStatusCode == java.net.HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder stringBuilder = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                bufferedReader.close();
                return stringBuilder.toString();

            } catch (Exception e) {
                Log.d(TAG, "postData : Error ", e);
                return new String("Error : " + e.getMessage());
            }
        }
    }

    public void showStamp() {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            gridViewString = new String[jsonArray.length()];
            gridViewImageId = new int[jsonArray.length()];

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(TAG_NAME);
                Log.d(TAG, "name -> " + name);
                // String cul_code = item.getString(TAG_CUL_CODE);
                String stamp_state = item.getString(TAG_STAMP_STATE);
                // 하드코딩중~~ 다른사람은 따라하지 마세요
                if(name.equals("영진전문대")){
                    tx1 = findViewById(R.id.stampn1);
                    tx1.setText(name);
                    if(stamp_state.equals("1")){
                        ix1 = findViewById(R.id.stempview1);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampok);
                        ix1.setImageDrawable(drawable);
                    }
                    else{
                        ix1 = findViewById(R.id.stempview1);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampview);
                        ix1.setImageDrawable(drawable);
                    }
                }
                else if(name.equals("동화사")){
                    tx2 = findViewById(R.id.stampn2);
                    tx2.setText(name);
                    if(stamp_state.equals("1")){
                        ix2 = findViewById(R.id.stempview2);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampok);
                        ix2.setImageDrawable(drawable);
                    }
                    else{
                        ix2 = findViewById(R.id.stempview2);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampview);
                        ix2.setImageDrawable(drawable);
                    }
                }
                else if(name.equals("도동서원")){
                    tx3 = findViewById(R.id.stampn3);
                    tx3.setText(name);
                    if(stamp_state.equals("1")){
                        ix3 = findViewById(R.id.stempview3);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampok);
                        ix3.setImageDrawable(drawable);
                    }
                    else{
                        ix3 = findViewById(R.id.stempview3);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampview);
                        ix3.setImageDrawable(drawable);
                    }
                }
                else if(name.equals("경상감영공원")){
                    tx4 = findViewById(R.id.stampn4);
                    tx4.setText(name);
                    if(stamp_state.equals("1")){
                        ix4 = findViewById(R.id.stempview4);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampok);
                        ix4.setImageDrawable(drawable);
                    }
                    else{
                        ix4 = findViewById(R.id.stempview4);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampview);
                        ix4.setImageDrawable(drawable);
                    }
                }
                //강원도 하드 코딩
                else if(name.equals("강릉 오죽헌")){
                    tx1 = findViewById(R.id.stampn1);
                    tx1.setText(name);
                    tx1 = findViewById(R.id.stampn1);
                    tx1.setText(name);
                    if(stamp_state.equals("1")){
                        ix1 = findViewById(R.id.stempview1);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampok);
                        ix1.setImageDrawable(drawable);
                    }
                    else{
                        ix1 = findViewById(R.id.stempview1);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampview);
                        ix1.setImageDrawable(drawable);
                    }
                }
                else if(name.equals("낙산사")){
                    tx2 = findViewById(R.id.stampn2);
                    tx2.setText(name);
                    if(stamp_state.equals("1")){
                        ix2 = findViewById(R.id.stempview2);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampok);
                        ix2.setImageDrawable(drawable);
                    }
                    else{
                        ix2 = findViewById(R.id.stempview2);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampview);
                        ix2.setImageDrawable(drawable);
                    }
                }
                else if(name.equals("월정사")){
                    tx3 = findViewById(R.id.stampn3);
                    tx3.setText(name);
                    if(stamp_state.equals("1")){
                        ix3 = findViewById(R.id.stempview3);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampok);
                        ix3.setImageDrawable(drawable);
                    }
                    else{
                        ix3 = findViewById(R.id.stempview3);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampview);
                        ix3.setImageDrawable(drawable);
                    }
                }
                else if(name.equals("보현사")){
                    tx4 = findViewById(R.id.stampn4);
                    tx4.setText(name);
                    if(stamp_state.equals("1")){
                        ix4 = findViewById(R.id.stempview4);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampok);
                        ix4.setImageDrawable(drawable);
                    }
                    else{
                        ix4 = findViewById(R.id.stempview4);
                        Drawable drawable = getResources().getDrawable(R.drawable.stampview);
                        ix4.setImageDrawable(drawable);
                    }
                }
                //여기까지가 하드코딩

                //                gridViewString[i] = name;
//
//                if(stamp_state == "1") {
//                    gridViewImageId[i] = R.drawable.stamp;
//                } else {
//                    gridViewImageId[i] = 0;
//                }
            }
//            GridViewActivity adapterView = new GridViewActivity(StampActivity.this, gridViewString, gridViewImageId);
//            gridView = (GridView)findViewById(R.id.stampView);
//            gridView.setAdapter(adapterView);



        } catch (JSONException e) {
            Log.d(TAG, "showStamp : ", e);
        }
    }

    public class GridViewActivity extends BaseAdapter {

        private Context mContext;
        private final String[] mGridViewString;
        private final int[] mGridViewImageId;

        public GridViewActivity(Context context, String[] gridViewString, int[] gridViewImageId) {
            mContext = context;
            mGridViewImageId = gridViewImageId;
            mGridViewString = gridViewString;
        }

        @Override
        public int getCount() {
            return mGridViewString.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(convertView == null) {
                view = new View(mContext);
                view = inflater.inflate(R.layout.stamp_listview, null);

                TextView textView = (TextView)view.findViewById(R.id.stampName);
                ImageView imageView = (ImageView)view.findViewById(R.id.stampImg);

                textView.setText(mGridViewString[position]);
                imageView.setImageResource(mGridViewImageId[position]);

            } else {
                view = (View)convertView;
            }

            return view;
        }
    }
}