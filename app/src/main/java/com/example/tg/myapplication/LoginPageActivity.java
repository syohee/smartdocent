package com.example.tg.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class LoginPageActivity extends AppCompatActivity {

    private static String TAG = "phptest_LoginPageActivity";

    EditText et_id, et_pw;
    String      id, pw;

    Intent intent;
    int lang_code;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);

        intent = getIntent();
        lang_code = intent.getIntExtra("lang_code", lang_code);

        et_id   =   (EditText)findViewById(R.id.login_id);
        et_pw   =   (EditText)findViewById(R.id.login_pw);


        // 문화재 페이지 버튼
        ImageButton cultureBtn = (ImageButton)findViewById(R.id.cultureBtn1);
        cultureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent cultureIntent = new Intent(LoginPageActivity.this, MainActivity.class);
                cultureIntent.putExtra("lang_code", lang_code);
                startActivity(cultureIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        // 마이페이지 버튼
        ImageButton mypageBtn = (ImageButton)findViewById(R.id.myPageBtn);
        mypageBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent mypageIntent = new Intent(LoginPageActivity.this, LoginPageActivity.class);
                mypageIntent.putExtra("lang_code", lang_code);
                startActivity(mypageIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        // 로그인 페이지 버튼
        Button loginBtn = (Button)findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                id      =   et_id.getText().toString();
                pw      =   et_pw.getText().toString();

                selectUser selectUser    =   new selectUser();
                selectUser.execute("http://35.184.38.112/loginPage.php", id, pw);


            }
        });

        // 회원 가입 페이지
        Button joinBtn = (Button)findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent joinIntent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(joinIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
    }

    public class selectUser extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(LoginPageActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            // mTextViewResult.setText(s);
            Log.d(TAG, "response -> " + s);

            if (s.equals("none")) {
                setContentView(R.layout.loginpage);

            } else {
                Intent myPageIntent = new Intent(LoginPageActivity.this, MyPageActivity.class);
                myPageIntent.putExtra("id", s);
                myPageIntent.putExtra("lang_code", lang_code);
                startActivity(myPageIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
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
                java.net.HttpURLConnection conn    =   (java.net.HttpURLConnection) serverURL.openConnection();



                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                OutputStream outputStream    =   conn.getOutputStream();

                outputStream.write(post.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = conn.getResponseCode();
                Log.d(TAG, "response code -> " + responseStatusCode);

                InputStream inputStream;

                if(responseStatusCode == java.net.HttpURLConnection.HTTP_OK) {
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
