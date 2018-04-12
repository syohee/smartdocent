package com.example.tg.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class JoinActivity extends AppCompatActivity {

    EditText    et_id, et_pw;
    String      id, pw;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joinpage);

        et_id   =   (EditText)findViewById(R.id.user_id);
        et_pw   =   (EditText)findViewById(R.id.user_pw);

    }

    public void joinBtn(View view) {
        id      =   et_id.getText().toString();
        pw      =   et_pw.getText().toString();

        InsertDB    join    =   new InsertDB();
        join.execute();
    }

    public class InsertDB extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String  param   =   "id = " + id + "&pw = " + pw + "";

            try {
                URL             serverURL   =   new URL("http://127.25.1.141/joinPage.php");
                HttpURLConnection   conn    =   (HttpURLConnection) serverURL.openConnection();

                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                OutputStream    outputStream    =   conn.getOutputStream();

                outputStream.write(param.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                InputStream     inputStream     =   conn.getInputStream();
                BufferedReader  bufferedReader  =   new BufferedReader(new InputStreamReader(inputStream));
                String          data            =   "";
                String          line            =   null;
                StringBuffer    stringBuffer    =   new StringBuffer();

                while((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                data    =   stringBuffer.toString().trim();

            } catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }
    }
}
