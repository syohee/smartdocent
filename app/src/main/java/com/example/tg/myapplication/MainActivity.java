package com.example.tg.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
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
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_IMG = "img";
    private static final String TAG_ADDR = "addr";

    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mListView;
    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        // mTextViewResult =   (TextView)findViewById(R.id.textViewResult);
        mListView   =   (ListView)findViewById(R.id.mainList);
        mArrayList  =   new ArrayList<>();

        GetData task = new GetData();
        task.execute("http://35.184.38.112/getMainList.php");

        /*TextView culId   =   (TextView)findViewById(R.id.culId);
        culId.setVisibility(View.INVISIBLE);*/


        // 문화재 페이지 버튼
        ImageButton cultureBtn = (ImageButton) findViewById(R.id.cultureBtn);
        cultureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent cultureIntent = new Intent(MainActivity.this, CultureActivity.class);
                startActivity(cultureIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        // 마이 페이지 버튼
        final ImageButton myPageBtn = (ImageButton) findViewById(R.id.myPageBtn);
        myPageBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myPageIntent = new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(myPageIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
    }

    // 문화재 리스트 출력
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            // mTextViewResult.setText(s);
            Log.d(TAG, "response -> " + s);

            if (s == null) {
                mTextViewResult.setText(errorString);
            } else {
                mJsonString = s;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String serverURL = strings[0];

            try {
                URL url =   new URL(serverURL);
                java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();

                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.connect();

                int responseStatusCode  =   con.getResponseCode();
                Log.d(TAG, "response code -> " + responseStatusCode);

                InputStream inputStream;

                if(responseStatusCode == con.HTTP_OK) {
                    inputStream = con.getInputStream();
                } else {
                    inputStream = con.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData : Error", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);
                String name = item.getString(TAG_NAME);
                String img = item.getString(TAG_IMG);
                String addr = item.getString(TAG_ADDR);

                // Log.d(TAG, id + ", " + name + ", " + addr);

                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_IMG, img);
                hashMap.put(TAG_ADDR, addr);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(MainActivity.this, mArrayList, R.layout.cul_listview,
                    new String[]{TAG_NAME, TAG_IMG, TAG_ADDR}, new int[] {R.id.culName, R.id.culImg, R.id.culAddr});

            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent cul_view = new Intent(MainActivity.this, CultureViewActivity.class);
                    cul_view.putExtra("cul_view_id", mArrayList.get(position).get(TAG_ID));
                    startActivity(cul_view);
                }
            });

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}

