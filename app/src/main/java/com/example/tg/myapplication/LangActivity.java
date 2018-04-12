package com.example.tg.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LangActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.langpage);

        // 메인 페이지 버튼 (언어 설정 후 이동)
        Button langBtn = (Button)findViewById(R.id.langBtn);
        langBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
    }
}
