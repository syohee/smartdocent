package com.example.tg.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MyPageActivity extends AppCompatActivity {

    private static String TAG = "phptest_MyPageActivity";

    Intent intent;
    String id;
    int lang_code;

    TextView userName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        intent = getIntent();
        id = intent.getStringExtra("id");
        lang_code = intent.getIntExtra("lang_code", lang_code);


        Log.d(TAG, "id -> " + id + ", lang_code -> " + lang_code);

        userName = (TextView)findViewById(R.id.userName);
        userName.setText(id);


        // 문화재 페이지 버튼
        ImageButton cultureBtn = (ImageButton)findViewById(R.id.cultureBtn);
        cultureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent cultureIntent = new Intent(getApplicationContext(), MainActivity.class);
                cultureIntent.putExtra("lang_code", lang_code);
                cultureIntent.putExtra("id", id);
                startActivity(cultureIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        // 스탬프 페이지 버튼
       ImageView stampBtn = (ImageView) findViewById(R.id.stampBtn);
        stampBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent stampIntent = new Intent(getApplicationContext(), StampActivity.class);
                stampIntent.putExtra("id", id);
                stampIntent.putExtra("lang_code", lang_code);
                startActivity(stampIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
    }
}
