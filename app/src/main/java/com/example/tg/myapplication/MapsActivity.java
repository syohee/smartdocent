package com.example.tg.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(35.896304, 128.621844);
    private static final int DEFAULT_ZOOM = 18;
    private static GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getSimpleName();

    public String cultural_code = "1";      // 1차 문화재 코드
    public String language_code = "1";      // 언어코드

    private Button b2, b3, b4, b5;          // 재생 정지 버튼*
    private ImageButton homeBt, currentBt, mypageBt;
    private static MediaPlayer music; // 스트리밍 객체 생성
    static String url = "http://35.184.38.112/";
    static String playFileName = "-1.mp3";  // 현재 재생중인 내레이션 파일 이름
    static String playFileCode = "-1";      // 현재 재생중인 내레이션 파일 코드
    static String newFileCode = "0";
    static String newFileName = "0.mp3";
    static String status = "stop";          // 재생 상태 (play, pause, stop)
    static String fileChange = "no";        // 파일 변경 체크 (재생 흐름 제어)
    static String playTime = "0";           // 현재 재생 시간
    static String fileLength = "0";         // 파일 총 길이
    static Double end_point = 0.0;          // 종료 지점
    static int setTime = 0;                 // (종료 지점 - 현재 재생 시간)*1000 (ms)
    static String prePoint = "0";           // 이전 해설 포인트
    static String nowPoint = "0";           // 현재 해설 포인트
    static String nowPointCode = "0";       // 현재 포인트의 요소 코드 ( ar인지 qr인지 체크)
    static String point = "0";              // 사용자위치로 찾은 해설 포인트 코드

    static String userStatus = "";          // 사용자 상태
    
    static int prepriority = 0;             // 이전 해설포인트 순서
    static int priority = 0;                // 현재 해설포인트 순서
    static int maxPriority = 4;             // 현재 문화재의 마지막 순서 //나중에 디비에서 계산해서 가져와야됨
    
    static String ete = "0";                // 예상 소요 시간
    static Boolean section_check = true;    // 구간해설파일 상태
    static String userLat;                  // 사용자의 현재 위도
    static String userLon;                  // 사용자의 현재 경도
    static Double nextLat;                  // 다음 해설포인트 위도
    static Double nextLon;                  // 다음 해설포인트 경도
    LocationManager mLM;                    // 실시간 위치 매니저
    NarrationAlgorismDB nadb;               // 디비 클래스
    View marker_root_view;
    TextView tv_marker;
    static String route;
    static String startRoute = "순서 : ";

    public static Context mContext;         // 현재 엑티비티 정보를 가져옴

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        b2 = (Button) findViewById(R.id.btn_play); //  정지 버튼 참조
        b3 = (Button) findViewById(R.id.button3);  // 재생 버튼 참조
        b4 = (Button) findViewById(R.id.arBtn);
        b5 = (Button) findViewById(R.id.qrBtn);
        homeBt = (ImageButton) findViewById(R.id.hoBtn);
        currentBt = (ImageButton) findViewById(R.id.cultBtn);
        mypageBt = (ImageButton) findViewById(R.id.myPaBtn);
        b3.setEnabled(false); // 재생버튼 비활성화
        b2.setEnabled(true); // 정지버튼 활성화
        b4.setEnabled(false); //ar버튼 비활성화
        b5.setEnabled(false); //qr버튼 비활성화
        nadb = new NarrationAlgorismDB();

        // 현재 문화재 코드
        // 선택된 언어 코드

        // 미디어 플레이어 객체 생성, 스트리밍 설정
        music = new MediaPlayer();
        music.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map); // 구글 지도 맵 api
        mapFragment.getMapAsync(this); // 현재 엑티비티에 구글지도 앱 띄우기
        GpsPermissionCheckForMashMallo(); // 권한 확인

        getGuideStart();        // 현재 문화재의 안내시작 멘트 가져오기
        getEndPriority();       // 현재 문화재의 마지막 순서 가져오기

        // 현재위치 서비스 매니저
        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        b3.setOnClickListener(new View.OnClickListener() { // 재생버튼 클릭시
            @Override
            public void onClick(View v) {
                if(status.equals("pause"))  // 일시정지 상태에서만 재생 가능
                    playExpl();
                b3.setEnabled(false); // 재생버튼 비활성화
                b2.setEnabled(true); // 정지버튼 활성화
            }
        });
        b2.setOnClickListener(new View.OnClickListener() { //정지 버튼 클릭시
            @Override
            public void onClick(View v) {
             b2.setEnabled(false);
             if(!status.equals("stop"))     // 정지상태에서는 일시정지 불가능
                music.pause();
             status="pause";
             b3.setEnabled(true);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b4.setEnabled(false);
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b5.setEnabled(false);
            }
        });
        homeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
        currentBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cultureIntent = new Intent(getApplicationContext(), CultureActivity.class);
                startActivity(cultureIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
        mypageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myPageIntent = new Intent(getApplicationContext(), MyPageActivity.class);
                startActivity(myPageIntent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        TextView Route;
        test Culture;
        Route = (TextView) findViewById(R.id.Route); // 경로보여주기

        Culture = new test();
        route = Culture.test("1");
        setRoute(route);
        Route.setText(startRoute);

        mContext = this;
    }

    // 현재 문화재의 마지막 순서 가져오기
    public void getEndPriority(){
        NarrationAlgorismDB.GetEndPriority gec = nadb.new GetEndPriority();
        try {
            String result = gec.execute(cultural_code).get();
            if(result != null){
                nadb.resultString = result;
                nadb.mGetEndPriorityList = new ArrayList<>();
                nadb.getEndPriorityResult();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    // 경로 이탈 멘트 파일 가져오기
    public void getWarning(){
        NarrationAlgorismDB.GetWarning gec = nadb.new GetWarning();
        try {
            String result = gec.execute(cultural_code, language_code).get();
            if(result != null){
                nadb.resultString = result;
                nadb.mGetWarningList = new ArrayList<>();
                nadb.getWarningResult();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    // 안내 종료 멘트 파일 가져오기
    public void getGuideEnd(){
        NarrationAlgorismDB.GetGuideEnd gec = nadb.new GetGuideEnd();
        try {
            String result = gec.execute(cultural_code, language_code).get();
            if(result != null){
                nadb.resultString = result;
                nadb.mGuideEndList = new ArrayList<>();
                nadb.getGuideEndResult();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    // 해설 포인트 가져오기
    public void getExplPoint() {
        NarrationAlgorismDB.GetExplPoint gec = nadb.new GetExplPoint();
        try {
            String result = gec.execute().get();
            if(result != null){
                nadb.resultString = result;
                nadb.getExplPointResult();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    // 구간해설 파일 가져오기
    public void getSection(){
        NarrationAlgorismDB.GetSection gec = nadb.new GetSection();
        try {
            String result = gec.execute(ete, nowPoint).get();
            if(result != null){
                nadb.resultString = result;
                nadb.mSectionList = new ArrayList<>();
                nadb.getSectionResult();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    // 종료 지점 가져오기
    public void getEndPoint() {
        NarrationAlgorismDB.GetEndPoint gec = nadb.new GetEndPoint();
        try {
            String result = gec.execute(playFileCode, playTime).get();
            if(result != null){
                nadb.resultString = result;
                nadb.mEndPointList = new ArrayList<>();
                nadb.getEndPointResult();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    // 현재 해설 포인트의 파일 코드, 파일 명 가져오기
    public void getFileNameNow() {
        NarrationAlgorismDB.GetFileNameNow gec = nadb.new GetFileNameNow();
//        gec.execute(nowPoint);
        try {
            String result = gec.execute(nowPoint).get();
            if(result != null){
                nadb.resultString = result;
                nadb.mFileNameNowList = new ArrayList<>();
                nadb.getFileNameNowResult();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    // 안내 시작 파일 명 가져오기
    public void getGuideStart() {
        NarrationAlgorismDB.GetGuideStart gec = nadb.new GetGuideStart();
        try {
            String result = gec.execute(cultural_code, language_code).get();
            if(result != null){
                nadb.resultString = result;
                nadb.mGuideStartList = new ArrayList<>();
                nadb.getGuideStartResult();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    // 사용자 위치 파악 해설포인트 밖이면 -1, 안이면 포인트코드
    public void getElement() {
        NarrationAlgorismDB.GetElement gec = nadb.new GetElement();
        gec.execute(userLat, userLon);
    }

    // 미디어 플레이어 재생
    public static void playExpl() {
        if(!status.equals("play") && fileChange.equals("no")){     // 재생중이 아니고 파일이 변경중이 아니면
            if(!playFileCode.equals(newFileCode)){   // 새로운 파일일 때
                fileChange = "yes";                 //fileChange.equals("yes")면 파일 변경중
//                status = "play";
                playFileName = newFileName;
                playFileCode = newFileCode;
                Log.d(TAG, "playExpl: 새로운 파일입니다. 미디어 플레이어 객체를 새롭게 작성합니다.");
                Log.d(TAG, "playExpl: 새롭게 재생 할 파일 명 : " + playFileName);
//            if(music != null){

//                music.pause();
                music.reset();
//                Log.d(TAG, "playExpl: 리셋");
//                music.release();
//                music = null;
//            }
//            if(music == null) {
//                Log.d(TAG, "playExpl: 뮤직 = null 객체 재 생성");
//                music = new MediaPlayer();
//                music.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            }

                try {
                    music.setDataSource(url + playFileName);
                    music.prepareAsync();
                    Log.d(TAG, "playExpl: 뮤직 파일 서버에서 가져와서 뮤직에 넣음 ");
                } catch (IOException e) {e.printStackTrace();
                    Log.d(TAG, "playExpl: music.setDataSource or prepareAsync() error");}
                music.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        Log.d(TAG, "onPrepared: 재생 시작 : " + playFileName);
                        status = "play";
                        Log.d(TAG, "playExpl: 스레드 시작");
                        Thread();
                        fileChange = "no";
                    }
                });
            }else if(status.equals("pause")){  //music.getCurrentPosition()/1000 < 999999 //playFinish.equals("no")
                // 동일한 파일인데 끝까지 다 들은 파일이 아닐때만 재생
                // 일시정지 했다가 재생할때만 동작
                music.start();
                Log.d(TAG, "playExpl: 파일 변경 없이 재생 시작 : " + playFileName);
                status = "play";
                Thread();
            }
        }
    }

    public void narration_algorism() {
        if (!point.equals("-1") && point != null && !point.equals("0")) {
            prePoint = nowPoint;
            nowPoint = point;
            if (!prePoint.equals(nowPoint)) {
                Log.d(TAG, "narration_algorism: 사용자가 새로운 해설포인트에 도착했습니다. 포인트 순서 : " + priority);

                if(prepriority + 1 != priority){
                    Log.d(TAG, "narration_algorism: 사용자가 경로를 이탈했습니다.");
                    userStatus = "off_course";
                    // 경로 이탈 멘트 가져오기
                    getWarning();
                }else{
                    // 현재 해설포인트 해설 파일 가져오기
                    getFileNameNow();
                }

                if(!newFileCode.equals("-2")) {
                    if (status.equals("play")) {
                        Log.d(TAG, "narration_algorism: 현재 해설중인 파일의 종료지점을 찾습니다.");
                        // 현재 재생시간 < min(종료지점) 쿼리
                        getEndPoint();
                        // 종료지점 - 현재 재생시간 -> 시간 후에 파일 종료

                    } else if (status.equals("pause")) {
                        Log.d(TAG, "narration_algorism: 해설이 일시정지 중입니다.");
                    } else if (status.equals("stop")) {
                        Log.d(TAG, "narration_algorism: 해설이 종료 상태 입니다.");
                        playExpl();
                    }
                }

            } else {
                Log.d(TAG, "narration_algorism: 사용자가 동일한 해설포인트 입니다. 포인트 순서 : " + priority);
                if (userStatus.equals("off_course")) {
                    getFileNameNow();
                    if (status.equals("play")) {
                        Log.d(TAG, "narration_algorism: 현재 해설중인 파일의 종료지점을 찾습니다.");
                        // 현재 재생시간 < min(종료지점) 쿼리
                        getEndPoint();
                        // 종료지점 - 현재 재생시간 -> 시간 후에 파일 종료

                    } else if (status.equals("pause")) {
                        Log.d(TAG, "narration_algorism: 해설이 일시정지 중입니다.");
                    } else if (status.equals("stop")) {
                        Log.d(TAG, "narration_algorism: 해설이 종료 상태 입니다.");
                        playExpl();
                        userStatus = "";
                    }
                }else{
                    if(status.equals("play")){}
                    else if(status.equals("pause")){}
                    else if(status.equals("stop")){
                        Log.d(TAG, "narration_algorism: 구간해설 입니다.");
                        // 구간해설
                        section();
                    }
                }
            }
            if(nowPointCode.equals("3")){   // ar존임
                b4.setEnabled(true);
            }else if(nowPointCode.equals("2")){ // qr존임
                b5.setEnabled(true);
            }
        } else {
            Log.d(TAG, "narration_algorism: 사용자가 해설포인트 밖입니다.");
            if(status.equals("play")){}
            else if(status.equals("pause")){}
            else if(status.equals("stop")){
                // 구간 해설
                Log.d(TAG, "narration_algorism: 구간해설 입니다.");
                section();
            }
        }
    }

    /// 구간해설
    public void section(){
        // 현재 사용자 위도 경도 (userLat, userLon)
        // 현재 사용자 해설 포인트 코드 (nowPoint)
        // 현재 해설포인트 순서 (priority)
        // 현재 문화재의 max 순서 (maxPriority)
        // if(현재 포인트 순서 ++ >= max순서)//
        // 현재 해설 순서 (priority) -> 다음 순서 해설 포인트 위도 경도 가져오기
        if(priority + 1 <= maxPriority){  // 다음 해설 포인트 있음
            Log.d(TAG, "section: 다음 해설포인트가 있습니다.");
            // 다음 해설 포인트 위도 경도 가져오기
            // 다음 해설 포인트 : priority(현재 해설포인트 순서) + 1번째
            nextLat = Double.parseDouble(nadb.mExplPointList.get(priority).get("latitude"));
            nextLon = Double.parseDouble(nadb.mExplPointList.get(priority).get("longitude"));

            // 현재 사용자 위도경도, 다음 해설포인트 위도경도 까지 거리 계산
            // 예상 도착시간 계산
            getDistance();
            // 예상도착시간(ete), 현재 해설포인트(nowPoint) 으로 구간해설파일 가져오기
            getSection();
            if(section_check == false){
                // 구간해설 파일 없음
                // 어떡하지?
            }
        }else{// 다음 해설 포인트 없음
            Log.d(TAG, "section: 다음 해설포인트가 없습니다.");
            // 종료 멘트 가져오기
            getGuideEnd();
            // 종료 멘트 재생
            playExpl();
        }
    }

    // 구간해설 거리 계산 함수(예상 소요 시간 : 초)
    public void getDistance(){
        // 사용자 위도 경도 (userLat, userLon)
        Location userLoc = new Location("p1");
        userLoc.setLatitude(Double.parseDouble(userLat));
        userLoc.setLongitude(Double.parseDouble(userLon));

        Location nextLoc = new Location("p2");
        nextLoc.setLatitude(nextLat);
        nextLoc.setLongitude(nextLon);

        Float distance = userLoc.distanceTo(nextLoc);  // 단위 : m
        Log.d(TAG, "getDistance: 현재위치(lat:"+ userLat +", lon:"+userLon+"에서 다음해설포인트(순서:"+(priority+1)+"번)까지 " + distance + "m 입니다.");
        ete = String.valueOf(Math.round(distance/0.33));           // 예상 소요 시간

    }

    public static void end_point_sleep(){
        try {
            if(status.equals("play") && setTime > 0){
                Thread.sleep(setTime);
                Log.d(TAG, "end_point_sleep: 종료지점 setTime : " + setTime);
                Log.d(TAG, "end_point_sleep: 종료지점 newFileName : " + newFileName);
                status = "stop";
                music.pause();
                Log.d(TAG, "end_point_sleep: 해설중인 파일을 종료하고 새로운 해설을 재생합니다.");
                playExpl();
            }else{
                Log.d(TAG, "end_point_sleep: 종료지점이 없습니다. 끝까지 재생합니다.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void Thread(){
        Runnable task = new Runnable(){
            public void run(){
                while(status.equals("play")){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    playTime = String.valueOf(music.getCurrentPosition()/1000);
                    fileLength = String.valueOf(music.getDuration()/1000);
                    Log.d(TAG, "run: 현재 재생시간 : " + music.getCurrentPosition()/1000 + "초 " +
                            "현재 재생 파일 총 길이 : " + fileLength);
                    // 파일 길이보다 재생시간이 넘어가면 종료
                    if(music.getCurrentPosition()/1000 >= music.getDuration()/1000){
                        Log.d(TAG, "run: 파일 다 들어서 종료됨");
                        status = "stop";
                        music.pause();
                    }
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setCustomMarkerView();
        //해설포인트
        getExplPoint();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        onAddMarker();
    }

    private void setCustomMarkerView(){
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.maker_layout, null);
        tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
    }
    // 마커, 원 추가
    public void onAddMarker() {
        ArrayList<HashMap<String, String>> explPointList =  nadb.mExplPointList;
        for (HashMap explPoint : explPointList) {
            Double lat = Double.parseDouble(explPoint.get("latitude").toString());
            Double lon = Double.parseDouble(explPoint.get("longitude").toString());
            String ele = explPoint.get("element_code").toString();
            String pri = explPoint.get("element_priority").toString();
            Log.d(TAG, "onAddMarker: lat : " + lat + ", lon : " + lon);
            LatLng yj = new LatLng(lat, lon);
            // element_code, latitude, longitude
            if(mMap != null) {
                if(ele.equals("5")){        // 해설 포인트
                    tv_marker.setText(pri);
                    mMap.addMarker(new MarkerOptions().position(yj).title(pri)
                            .icon(BitmapDescriptorFactory
                            .fromBitmap(createDrawableFromView(this, marker_root_view))));
                    CircleOptions circle = new CircleOptions().center(yj)  // yj가 원점
                            .radius(10)        // 반지름 단위 : m
                            .strokeWidth(0f)    // 선 너비 / 0f : 선 없음
                            .fillColor(Color.parseColor("#B2CCFF"));  // 배경색
                    mMap.addCircle(circle); // 반경 추가
                }else if(ele.equals("4")){  // information
                    mMap.addMarker(new MarkerOptions().position(yj).title("information")
                        .icon(BitmapDescriptorFactory
                        .fromBitmap(resizeMapIcons("information", 70, 70
                        ))));
                }else if(ele.equals("3")){  // ar
                    mMap.addMarker(new MarkerOptions().position(yj).title("AR")
                        .icon(BitmapDescriptorFactory
                        .fromBitmap(resizeMapIcons("aricon", 70, 70
                        ))));
                }else if(ele.equals("2")){  // qr
                    mMap.addMarker(new MarkerOptions().position(yj).title("QR")
                        .icon(BitmapDescriptorFactory
                        .fromBitmap(resizeMapIcons("qricon", 50, 50
                        ))));
                }else if(ele.equals("1")){  // 화장실
                    mMap.addMarker(new MarkerOptions().position(yj).title("restroom")
                        .icon(BitmapDescriptorFactory
                        .fromBitmap(resizeMapIcons("restroom", 70, 70
                        ))));
                }
            }
        }
        Log.d(TAG, "onAddMarker: 마커 & 반경 생성 완료");
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    public void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
//            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            //사용자 위도, 경도 저장
                            userLat = Double.toString(mLastKnownLocation.getLatitude());
                            userLon = Double.toString(mLastKnownLocation.getLongitude());
//                            Log.d(TAG, "onComplete: 현재 좌표 : Lat - " + userLat + ", Lon - " + userLon);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
//            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void locationUpdate(){
        mLM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, mLocationListener);
        mLM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, mLocationListener);
    }

    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // 위치 갱신 시 이벤트 발생
            Log.d(TAG, "onLocationChanged: 사용자가 이동하고 있습니다.");
            getElement();
            narration_algorism();
            getDeviceLocation();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                locationUpdate();
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //권한 확인
    public void GpsPermissionCheckForMashMallo() {//위치 권한 확인

        //마시멜로우 버전 이하면 if문에 걸리지 않습니다.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("GPS 사용 허가 요청");
            alertDialog.setMessage("앰버요청 발견을 알리기위해서는 사용자의 GPS 허가가 필요합니다.\n('허가'를 누르면 GPS 허가 요청창이 뜹니다.)");
            // OK 를 누르게 되면 설정창으로 이동합니다.
            alertDialog.setPositiveButton("허가",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(com.example.tg.myapplication.MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                        }
                    });
            // Cancle 하면 종료 합니다.
            alertDialog.setNegativeButton("거절",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
        }
    }

    // 마커 아이콘 모양/사이즈 변경
    public Bitmap resizeMapIcons(String iconName, int width, int height) {//마커이미지 변화
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    // 문화재 경로
    public void setRoute(String mJsonString){
        String TAG = "phpquerytest";
        final String TAG_JSON="webnautes";
        final String TAG_cultural_name = "cultural_name";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);


            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                startRoute += i+1 +". " + item.getString(TAG_cultural_name) +" ";

            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }

    //현재 문화재 반경 빨간색 변경
    public void nextcultural(int priority){
        GoogleMap googleMap = mMap;
        ArrayList<HashMap<String, String>> explPointList =  nadb.mExplPointList;
        for (HashMap explPoint : explPointList) {
            Double lat = Double.parseDouble(explPoint.get("latitude").toString());
            Double lon = Double.parseDouble(explPoint.get("longitude").toString());
            String ele = explPoint.get("element_code").toString();
            String pri = explPoint.get("element_priority").toString();
            Log.d(TAG, "onAddMarker: lat : " + lat + ", lon : " + lon);
            LatLng yj = new LatLng(lat, lon);
            if(pri.equals(String.valueOf(priority)) && ele.equals("5")){
                tv_marker.setText(pri);
                googleMap.addMarker(new MarkerOptions().position(yj).title(pri)
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(createDrawableFromView(this, marker_root_view))));
                CircleOptions circle = new CircleOptions().center(yj)  // yj가 원점
                        .radius(10)        // 반지름 단위 : m
                        .strokeWidth(0f)    // 선 너비 / 0f : 선 없음
                        .fillColor(Color.parseColor("#ff0000"));  // 배경색
                googleMap.addCircle(circle); // 반경 추가
            }
            else if(ele.equals("5")){
                tv_marker.setText(pri);
                mMap.addMarker(new MarkerOptions().position(yj).title(pri)
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(createDrawableFromView(this, marker_root_view))));
                CircleOptions circle = new CircleOptions().center(yj)  // yj가 원점
                        .radius(10)        // 반지름 단위 : m
                        .strokeWidth(0f)    // 선 너비 / 0f : 선 없음
                        .fillColor(Color.parseColor("#B2CCFF"));  // 배경색
                mMap.addCircle(circle); // 반경 추가

            }
        }
    }

    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
