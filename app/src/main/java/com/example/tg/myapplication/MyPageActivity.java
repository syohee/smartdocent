package com.example.tg.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MyPageActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        // 메인 페이지 버튼
        ImageButton homeBtn = (ImageButton)findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        // 문화재 페이지 버튼
        ImageButton cultureBtn = (ImageButton)findViewById(R.id.cultureBtn);
        cultureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent cultureIntent = new Intent(getApplicationContext(), CultureActivity.class);
                startActivity(cultureIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        // 회원 가입 버튼
       Button joinBtn = (Button)findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent joinIntent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(joinIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
        // 로그인

    }
}
