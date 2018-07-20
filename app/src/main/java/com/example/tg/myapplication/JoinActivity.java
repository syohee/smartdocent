package com.example.tg.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class JoinActivity extends AppCompatActivity {

    private static String TAG = "phptest_JoinActivity";

    EditText    et_id, et_pw;
    String      id, pw, id1;
    Intent intent;
    int lang_code;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joinpage);

        et_id   =   (EditText)findViewById(R.id.user_id);
        et_pw   =   (EditText)findViewById(R.id.user_pw);
        intent = getIntent();
        id1 = intent.getStringExtra("id");
        lang_code = intent.getIntExtra("lang_code", lang_code);


        // 문화재 페이지 버튼
        ImageButton cultureBtn = (ImageButton)findViewById(R.id.cultureBtn);
        cultureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent cultureIntent = new Intent(JoinActivity.this, MainActivity.class);
                cultureIntent.putExtra("lang_code", lang_code);
                startActivity(cultureIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        // 마이페이지 버튼
        ImageButton mypageBtn = (ImageButton)findViewById(R.id.myPageBtn);
        cultureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent mypageIntent = new Intent(getApplicationContext(), LoginPageActivity.class);
                mypageIntent.putExtra("lang_code", lang_code);
                startActivity(mypageIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

    }

    public void joinBtn(View view) {
        id      =   et_id.getText().toString();
        pw      =   et_pw.getText().toString();

        InsertDB    join    =   new InsertDB();
        join.execute("http://35.184.38.112/joinPage.php", id, pw);
    }

    public class InsertDB extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(JoinActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            // mTextViewResult.setText(s);
            Log.d(TAG, "response -> " + s);

            if (s.equals("success")) {
                Intent loginPageIntent = new Intent(JoinActivity.this, LoginPageActivity.class);
                startActivity(loginPageIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            } else {

            }
        }

        @Override
        protected String doInBackground(String... strings) {

            String url = strings[0];
            String id = strings[1];
            String pw = strings[2];

            String post = "id=" + id + "&pw=" + pw;

            try {
                URL serverURL   =   new URL(url);
                HttpURLConnection   conn    =   (HttpURLConnection) serverURL.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                OutputStream    outputStream    =   conn.getOutputStream();

                outputStream.write(post.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = conn.getResponseCode();
                Log.d(TAG, "response code -> " + responseStatusCode);

                InputStream inputStream;

                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = conn.getInputStream();
                }
                else{
                    inputStream = conn.getErrorStream();
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

            } catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }
    }
}
