package com.example.tg.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CultureActivity extends AppCompatActivity {
    private static String TAG = "phpquerytest";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_cultural_name = "cultural_name";
    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mListViewList;
    EditText mEditTextSearchKeyword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.culturepage);
        try {

            // 마이 페이지 버튼
            ImageButton myPageBtn = (ImageButton) findViewById(R.id.myPageBtn);
            myPageBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent myPageIntent = new Intent(getApplicationContext(), MyPageActivity.class);
                    startActivity(myPageIntent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                }
            });
            JKDB jkdb = new JKDB();
            String test = jkdb.test();
            JSONObject jsonObject = new JSONObject(test);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            mListViewList = (ListView) findViewById(R.id.listView_main_list);
            mArrayList = new ArrayList<>();
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject ddObject = jsonArray.getJSONObject(i);
                String cultural_name = ddObject.getString("cultural_name");
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(TAG_cultural_name,cultural_name);
                mArrayList.add(hashMap);
            }
            ListAdapter adapter = new SimpleAdapter(
                    this, mArrayList, R.layout.item_list,
                    new String[]{TAG_cultural_name},
                    new int[]{R.id.textView_list_id}

            );
            mListViewList.setAdapter(adapter);

            mListViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position==0)
                    {
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade, R.anim.hold);
                    }

                }
            });

        }catch (Exception e){

        }
    }


}

