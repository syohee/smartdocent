package com.example.tg.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;


public class CultureViewActivity extends AppCompatActivity {
    private static String TAG = "phptest_CultureViewActivity";

    private static final String TAG_JSON = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_IMG = "img";
    private static final String TAG_ADDR = "addr";
    private static final String TAG_EXPL = "expl";
    private Button explBtn;

    Intent intent;
    String cul_view_id;
    String lang_code = "1";
    ArrayList<HashMap<String, String>> mArrayList;
    String mJsonString;

    ImageView cul_view_img;
    TextView cul_view_name;
    TextView cul_view_addr;
    TextView cul_view_expl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cul_view);

        intent = getIntent();
        cul_view_id = intent.getStringExtra("cul_view_id");
        Log.d(TAG, "cul_id ->" + cul_view_id);

        PostData data = new PostData();
        data.execute(cul_view_id, lang_code);

        // 메인 페이지 버튼
        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent homeIntent = new Intent(CultureViewActivity.this, MainActivity.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        // 문화재 페이지 버튼
        ImageButton cultureBtn = (ImageButton) findViewById(R.id.cultureBtn);
        cultureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent cultureIntent = new Intent(CultureViewActivity.this, CultureActivity.class);
                startActivity(cultureIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        // 마이 페이지 버튼
        final ImageButton myPageBtn = (ImageButton) findViewById(R.id.myPageBtn);
        myPageBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myPageIntent = new Intent(CultureViewActivity.this, MyPageActivity.class);
                startActivity(myPageIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
        explBtn = (Button) findViewById(R.id.explBtn);
        explBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MapIntent = new Intent(CultureViewActivity.this, MapsActivity.class);
                startActivity(MapIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
    }

    private class PostData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CultureViewActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            Log.d(TAG, "Post response -> " + s);

            if (s == null) {
                // mTextViewResult.setText(errorString);
            } else {
                mJsonString = s.substring(4);
                Log.d(TAG, "jsonString -> " + mJsonString);
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String culId = strings[0];
            String langCode = strings[1];

            String serverUrl = "http://35.184.38.112/culturalView.php";
            String post = "cul_view_id=" + culId + "&lang_code=" + langCode;

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

    private void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            cul_view_name = (TextView)findViewById(R.id.cul_view_name);
            cul_view_addr = (TextView)findViewById(R.id.cul_view_addr);
            cul_view_expl = (TextView)findViewById(R.id.cul_view_expl);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(TAG_NAME);
                String img = item.getString(TAG_IMG);
                String addr = item.getString(TAG_ADDR);
                String expl = item.getString(TAG_EXPL);

                cul_view_name.setText(name);
                cul_view_addr.setText(addr);
                cul_view_expl.setText(expl);

                // Log.d(TAG, id + ", " + name + ", " + addr);

                /*HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_IMG, img);
                hashMap.put(TAG_ADDR, addr);
                hashMap.put(TAG_EXPL, expl);

                mArrayList.add(hashMap);*/
            }

            /*ListAdapter adapter = new SimpleAdapter(CultureViewActivity.this, mArrayList, R.layout.cul_listview,
                    new String[]{TAG_NAME, TAG_IMG, TAG_ADDR, TAG_ID}, new int[]{R.id.culName, R.id.culImg, R.id.culAddr, R.id.culId});*/


        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}

