package com.example.tg.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class LangActivity extends AppCompatActivity {

    private static String TAG = "phptest_LangActivity";
    int lang_code = 0;
    TextView textView;
    ImageButton prevBtn, nextBtn;
    Animation slide_in_left, slide_in_right, slide_out_left, slide_out_right;
    ViewFlipper viewFlipper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.langpage);

        prevBtn = (ImageButton)findViewById(R.id.prevBtn);
        nextBtn = (ImageButton)findViewById(R.id.nextBtn);
        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);

        slide_in_left = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slide_in_right = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slide_out_left = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slide_out_right = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setInAnimation(slide_in_right);
                viewFlipper.setOutAnimation(slide_out_left);
                viewFlipper.showPrevious();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setInAnimation(slide_in_left);
                viewFlipper.setOutAnimation(slide_out_right);
                viewFlipper.showNext();
            }
        });

        // 메인 페이지 버튼 (언어 설정 후 이동)
        Button langBtn = (Button)findViewById(R.id.langBtn);
        langBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);

                switch (viewFlipper.getDisplayedChild()) {
                    case 0: lang_code = 1;
                    break;
                    case 1: lang_code = 2;
                    break;
                    case 2: lang_code = 3;
                    break;
                    case 3: lang_code = 4;
                }

                mainIntent.putExtra("lang_code", lang_code);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);

                Log.d(TAG, "lang_code -> " + lang_code);
            }
        });
    }
}
