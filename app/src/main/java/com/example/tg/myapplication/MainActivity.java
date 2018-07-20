package com.example.tg.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_IMG = "img";
    private static final String TAG_ADDR = "addr";
    private static final String TAG_ADD = "addr";

    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mListView;
    String mJsonString;

    Intent intent;
    int lang_code;
    String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        // mTextViewResult =   (TextView)findViewById(R.id.textViewResult);
        mListView   =   (ListView)findViewById(R.id.mainList);
        intent = getIntent();
        lang_code = intent.getIntExtra("lang_code", lang_code);
        user_id = intent.getStringExtra("id");

        Log.d(TAG, "user_id -> " + user_id);

        mArrayList  =   new ArrayList<>();

        GetData task = new GetData();
        task.execute("http://35.184.38.112/getMainList.php", String.valueOf(lang_code));

        // 문화재 페이지 버튼
        ImageButton cultureBtn = (ImageButton) findViewById(R.id.cultureBtn);
        cultureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent cultureIntent = new Intent(MainActivity.this, MainActivity.class);
                cultureIntent.putExtra("lang_code", lang_code);
                startActivity(cultureIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        // 마이 페이지 버튼
        final ImageButton myPageBtn = (ImageButton) findViewById(R.id.myPageBtn);
        myPageBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(user_id != null) {
                    Intent myPageIntent = new Intent(MainActivity.this, MyPageActivity.class);
                    myPageIntent.putExtra("lang_code", lang_code);
                    myPageIntent.putExtra("id", user_id);
                    startActivity(myPageIntent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                }
                Intent myPageIntent = new Intent(MainActivity.this, LoginPageActivity.class);
                myPageIntent.putExtra("lang_code", lang_code);
                myPageIntent.putExtra("id", user_id);
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
            String langCode = strings[1];

            Log.d(TAG, "lang_code -> " + langCode);

            String post = "lang_code=" + langCode;

            try {
                URL url =   new URL(serverURL);
                java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();

                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.connect();

                OutputStream outputStream = con.getOutputStream();
                outputStream.write(post.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

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


                String addr = item.getString(TAG_ADD);

                // Log.d(TAG, id + ", " + name + ", " + addr);

                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_IMG, img);
                hashMap.put(TAG_ADDR, addr);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(MainActivity.this, mArrayList, R.layout.cul_listview,
                    new String[]{TAG_NAME, TAG_IMG, TAG_ADDR }, new int[] {R.id.culName, R.id.culImg, R.id.culAddr});

            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent cul_view = new Intent(MainActivity.this, CultureViewActivity.class);
                    cul_view.putExtra("cul_view_id", mArrayList.get(position).get(TAG_ID));
                    cul_view.putExtra("lang_code", lang_code);
                    cul_view.putExtra("id", user_id);
                    startActivity(cul_view);
                }
            });


            //SimpleAdapter사용할때 이미지 입히는 작업이지만 안되서 하드코딩함
            ((SimpleAdapter)adapter).setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object o, String s) {
                    if(view.getId() == R.id.culImg){
                        ImageView imageView = (ImageView)view;
//                        Drawable drawable = (Drawable)o;
                        if(s.equals("yyy.jpg")) {
                            Drawable drawable = getResources().getDrawable(R.drawable.cau1);
                            imageView.setImageDrawable(drawable);
                        }
                        else if(s.equals("cau2.jpg")){
                            Drawable drawable = getResources().getDrawable(R.drawable.cau2);
                            imageView.setImageDrawable(drawable);

                        }
                        else if(s.equals("cau3.jpg")){
                            Drawable drawable = getResources().getDrawable(R.drawable.cau3);
                            imageView.setImageDrawable(drawable);

                        }
                        else{
                            Drawable drawable = getResources().getDrawable(R.drawable.cau4);
                            imageView.setImageDrawable(drawable);
                        }

                        return true;
                    }
                    return false;
                }
            });

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}

