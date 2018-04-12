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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap Current, mMap; // 현재위치와 문화재위치 마커 객체
    public String first_cultural_code = "1"; // 1차 문화재 코드
    private Button b1, b2, b3; // 재생 정지 버튼
    private ImageButton homeBt, currentBt, mypageBt;
    private SeekBar seekbar; // 스크롤
    View marker_root_view;
    private TextView tx1, tx2, tv_marker; //  오디오 시작 시간과 끝시간
    private double startTime = 0; // 오디오 시작 타임
    private double finalTime = 0; // 오디오 정지했을때 타임
    private static int UserLocation = 0;// 현재 유저가 몇번째 문화재를 보고 있는지에 대한 정보
    public static int oneTimeOnly = 0;
    private static int AudioCondition=0;
    private  static MediaPlayer mediaPlayer = new MediaPlayer(); // 스트리밍 객체 생성



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        b2 = (Button) findViewById(R.id.button2); //  정지 버튼 참조
        b3 = (Button) findViewById(R.id.button3); // 재생 버튼 참조
        homeBt = (ImageButton) findViewById(R.id.hoBtn);
        currentBt = (ImageButton) findViewById(R.id.cultBtn);
        mypageBt = (ImageButton) findViewById(R.id.myPaBtn);
        seekbar = (SeekBar) findViewById(R.id.seekBar); // 재생바
        seekbar.setClickable(false); // 재생바 비활성화
        b3.setEnabled(false); // 재생버튼 비활성화
        b2.setEnabled(false); // 정지버튼 비활성화
        tx1 = (TextView) findViewById(R.id.textView2); // 오디오 현 재생 시간
        tx2 = (TextView) findViewById(R.id.textView3); // 오디오 끝 시간
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map); // 구글 지도 맵 api
        mapFragment.getMapAsync(this); // 현재 엑티비티에 구글지도 앱 뛰우기
        GpsPermissionCheckForMashMallo(); // 권한 확인
        Log.d("Main", "onCreate");
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 경도위도 받아오는 서비스 만들기
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 휴대폰 GPS권한 확인
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        b3.setOnClickListener(new View.OnClickListener() { // 재생버튼 클릭시
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                b3.setEnabled(false); // 재생버튼 비활성화
                b2.setEnabled(true); // 정지버튼 활성화

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                if (oneTimeOnly == 0) {
                    seekbar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }
                tx2.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );

                tx1.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                );

                seekbar.setProgress((int) startTime);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() { //정지 버튼 클릭시
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                b2.setEnabled(false); // 정지버튼 비활성화
                b3.setEnabled(true); // 재생버튼 활성화
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

    }




    public void onMapReady(final GoogleMap googleMap) {
        Current = googleMap; //현재위치 마커
        mMap = googleMap;  // 문화재 위치 마커
//        // Add a marker in Sydney and move the camera
           googleMap.getUiSettings().setScrollGesturesEnabled(false); // 구글맵 스크롤 비활성화

         googleMap.getUiSettings().setZoomGesturesEnabled(false); // 구글맵 줌 비활성화
            final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    LatLng sydney = new LatLng(lat, lng);
                    test server = new test();
                    String test = server.test(first_cultural_code);
                    Current.addMarker(new MarkerOptions().position(sydney).title("내위치")
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("navigation",100,100
                            ))));
                    Current.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    Current.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
                    drawMarker(location, test);

                }

                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                public void onProviderEnabled(String provider) {

                }

                public void onProviderDisabled(String provider) {

                }
            };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // 수동으로 위치 구하기
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLatitude();
            double lat = lastKnownLocation.getLatitude();


            Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
        }
}

    // 마크 생성
    private void drawMarker(Location location, String mJsonString){
     String TAG_JSON="webnautes";
        try {
            Current.clear();//현재위치 초기화
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());//현재위치 다시받아오기
            Current.addMarker(new MarkerOptions().position(currentPosition).title("내위치")
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("navigation",100,100))));
            //현재위치에 대한정보로 지도앱에 다시한번 마크 찍기
            JSONObject jsonObject = new JSONObject(mJsonString);//서버의 DB정보를 스트링 형식으로 넣고 jsonObject 객체 생성
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);//jsonObject를 참조하는 jsonArray 생성
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject ddObject = jsonArray.getJSONObject(i);
                String element = ddObject.getString("element_detail_code");
                String priority = ddObject.getString("element_priority");
                String ele_code = ddObject.getString("element_code");
                String lat = ddObject.getString("latitude");
                String lon = ddObject.getString("longitude");
                LatLng Heritage = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                if(ele_code.equals("5")) {
                    marker_root_view = LayoutInflater.from(this).inflate(R.layout.maker_layout, null);
                    tv_marker = (TextView)marker_root_view
                            .findViewById(R.id.tv_marker);
                    tv_marker.setText(priority);
                     mMap.addMarker(new MarkerOptions().position(Heritage).title(priority)
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(createDrawableFromView(this,marker_root_view))));

                }
                else if(ele_code.equals("4")){
                    mMap.addMarker(new MarkerOptions().position(Heritage).title("information")
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(resizeMapIcons("information", 70, 70
                            ))));

                }
                else if(ele_code.equals("1")){
                    mMap.addMarker(new MarkerOptions().position(Heritage).title("restroom")
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(resizeMapIcons("restroom", 70, 70
                            ))));

                }


                Current.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                Current.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18));
            }
            LocationLookup asd = new LocationLookup();
            String nare = asd.lookup(location, jsonArray);
            if (nare.equalsIgnoreCase("0") == false && AudioCondition == 0) {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource("http://35.184.38.112/" + nare + ".mp3");
                    mediaPlayer.prepareAsync();
                    AudioCondition++;
                    Thread.sleep(2000);
                    StartAudio(mediaPlayer);

            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) { //노래가 끝났을때 실행하는 메소드
                    b2.setEnabled(false);
                    b3.setEnabled(false);
                    AudioCondition=0;
                }
            });

        }catch (Exception e){
            e.printStackTrace();

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

    public void StartAudio(MediaPlayer md) {//스트리밍 시작
            b3.setEnabled(false);
            b2.setEnabled(true);
            md.start();

            finalTime = md.getDuration();
            startTime = md.getCurrentPosition();

            if (oneTimeOnly == 0) {
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }
            tx2.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
            );

            tx1.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
            );

            seekbar.setProgress((int) startTime);
        }
    public Bitmap resizeMapIcons(String iconName, int width, int height){//마커이미지 변화
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
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

}
