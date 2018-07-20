package com.example.tg.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class ScanActivity extends AppCompatActivity {
    private static String TAG = "scanActivity";

    private IntentIntegrator qrScan;
    private TextView qrExpl;

    private Intent intent;

    private int lang_code;
    private String id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        id = intent.getStringExtra("id");
        lang_code = intent.getIntExtra("lang_code", lang_code);

        Log.d(TAG, "id -> " + id);

        qrScan = new IntentIntegrator(this);
        qrScan.setPrompt("코드를 찍어주세요.");
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Log.d(TAG, "qr 정보 없음");
            } else { //QR코드, 내용 존재
                Log.d(TAG, "qr 스캔 완료");
                try {
                    /* QR 코드 내용*/
                    /*String temp = result.getContents();
                    Log.v(TAG, temp);
                    Toast.makeText(getApplicationContext(), result.getContents(), Toast.LENGTH_LONG).show();*/

                    /*Intent stampIntent = new Intent(ScanActivity.this, StampActivity.class);
                    stampIntent.putExtra("qrExpl", result.getContents());
                    stampIntent.putExtra("lang_code", lang_code);
                    stampIntent.putExtra("id", id);
                    startActivity(stampIntent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);*/

                    InsertStamp insertStamp = new InsertStamp();
                    insertStamp.execute("http://35.184.38.112/stampInsert.php", id, result.getContents());

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "QR code fail");

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public class InsertStamp extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = progressDialog.show(ScanActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            Log.d(TAG, "response -> " + s);

            if(s.equals("success")) {
                Intent mapsIntent = new Intent(ScanActivity.this, MapsActivity.class);
                mapsIntent.putExtra("lang_code", lang_code);
                mapsIntent.putExtra("id", id);

                AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                builder.setTitle("SUCCESS");
                builder.setMessage("스탬프가 등록되었습니다.");
                builder.setCancelable(true);
                builder.show();


                startActivity(mapsIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);

            } else {

            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            String user_id = strings[1];
            String cul_code = strings[2];

            Log.d(TAG, "id -> " + user_id + ", cul_code -> " + cul_code);

            String post = "id = " + user_id + "&cul_code = " + cul_code;

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

