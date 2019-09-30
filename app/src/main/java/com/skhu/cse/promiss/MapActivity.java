package com.skhu.cse.promiss;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.MarkerIcons;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;
import com.skhu.cse.promiss.Items.AppointmentData;
import com.skhu.cse.promiss.Items.UserData;
import com.skhu.cse.promiss.Items.UserItem;
import com.skhu.cse.promiss.Service.PromissService;
import com.skhu.cse.promiss.database.BasicDB;
import com.skhu.cse.promiss.server.GetJson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    NaverMap map;
    RelativeLayout acceptLayout;
    TextView time_textView;
    CircleOverlay circle; //원 자기장
    ArrayList<Marker> markerArrayList=new ArrayList<>();
    Marker appointMarker;

    RecyclerView recyclerView;
    ArrayList<UserItem> arrayList;
    MemberAdapter adapter;
    boolean isOnce = false;

    final public int ADD_APPOINTMENT = 2002;
    boolean isAppointment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        time_textView = findViewById(R.id.map_appoint_time);
        acceptLayout = findViewById(R.id.map_invite_layout);
        if(BasicDB.getAppoint(getApplicationContext())==-1)
        CheckAppointment();
        else
        GetAppointment();

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }



        final String[] menu={"로그아웃","비밀번호 변경","회원 탈퇴"};

        TextView name = findViewById(R.id.map_title_user_name);
        name.setText("ID: "+ UserData.shared.getName());

        findViewById(R.id.map_add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView textView = (TextView) view;

                if (textView.getText().toString().equals("약속 상세보기")) {
                    Intent intent = new Intent(MapActivity.this,DetailActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MapActivity.this, AddAppointmentActivity.class);
                    startActivityForResult(intent,ADD_APPOINTMENT);
                }
            }
        });

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        //setting 버튼 클릭
        findViewById(R.id.map_person_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MapActivity.this,R.style.PromissAlertDialogStyle)
                        .setItems(menu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                               if(i==0){
                                   BasicDB.setId(getApplicationContext(),-1);
                                   BasicDB.setAppoint(getApplicationContext(),-1);
                                   Intent intent = new Intent(MapActivity.this, LoginActivity.class);
                                   startActivity(intent);
                                   finish();
                               }
                                else if(i==2) {
                                    Intent intent = new Intent(MapActivity.this, DeleteActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                builder.show();
            }
        });
        mapFragment.getMapAsync(this);
    }

    public void CalculateTime(String date,String time) //시간 계산
    {
        Calendar temp = Calendar.getInstance();



        String[] date_S= date.split("-");


        String[] time_S = time.split(":");


            GregorianCalendar now = new GregorianCalendar(temp.get(Calendar.YEAR),temp.get(Calendar.MONTH)+1,temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.MINUTE));

            GregorianCalendar appoint = new GregorianCalendar(Integer.parseInt(date_S[0]),Integer.parseInt(date_S[1]),Integer.parseInt(date_S[2]),Integer.parseInt(time_S[0]),Integer.parseInt(time_S[1]));

            long diff=appoint.getTimeInMillis()-now.getTimeInMillis();
        long sec = diff / 1000;
        long min = diff / (60 * 1000);
        long hour = diff / (60 * 60 * 1000);
        long day = diff / (24 * 60 * 60 * 1000);


        Timer timer= new Timer();
        timer.schedule(new AppointTimer((int)((hour - day*24)+(24*day)),(int)(min - hour*60), (int)(sec - min*60)),0,1000);
    }

    public void GetAppointment(){ //약속 정보 가져오기

        isAppointment = true;
        new Thread()
        {
            @Override
            public void run() {
                GetJson json = GetJson.getInstance();
                json.requestPost("api/Appointment/getAppointment",appoint,"id",BasicDB.getAppoint(getApplicationContext())+"");
            }
        }.run();
    }

    public void SetGameSetiing(int id){




        recyclerView = findViewById(R.id.map_member_list);
        MapActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.map_add_btn).setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MapActivity.this);
                linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        });

        if(arrayList==null)
        arrayList=new ArrayList<>();
        arrayList.add(0,new UserItem(-1,"목적지",false));
        adapter=new MemberAdapter(this,arrayList);
        adapter.setClickEvent(new MemberAdapter.ClickEvent() {
            @Override
            public void OnClick(View view, int position) {
                Marker marker;
                if(position==0){
                    marker = appointMarker;
                }else {
                     marker= markerArrayList.get(position-1);
                }
                double latitude = marker.getPosition().latitude;
                double longitude = marker.getPosition().longitude;

                if(latitude==0&&longitude==0){

                    MapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           Toast.makeText(MapActivity.this,"친구들 위치가 확인이 안됩니다",Toast.LENGTH_LONG).show();
                        }
                    });


                }else {
                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(latitude, longitude))
                            .animate(CameraAnimation.Easing);
                    map.moveCamera(cameraUpdate);
                }
            }
        });


        MapActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                recyclerView.setAdapter(adapter);
            }
        });




        PusherOptions options = new PusherOptions();
        options.setCluster("ap3");
        Pusher pusher = new Pusher("cb4bcb99bfc3727bdfb0", options);

        Channel channel = pusher.subscribe("ProMiss");

        channel.bind("event_game"+id, new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event)
            {
                String data= event.getData();
                Log.d("data",data);

                boolean isInit = false;
                if(arrayList.size()==1) isInit = true;
                try{
                    JSONObject object = new JSONObject(data);

                    JSONObject appoint = object.getJSONObject("appointment");

                    String name = appoint.getString("name");
                    double latitude = appoint.getDouble("latitude");
                    double longitude = appoint.getDouble("longitude");
                    double radius = appoint.getDouble("radius");

                    MapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(appointMarker==null)
                            SetAppointmentMarker(latitude,longitude,name);
                            SetCircle(latitude,longitude,radius); //원 생성
                        }
                    });

                    JSONArray members = object.getJSONArray("members");

                    MapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(markerArrayList.size()>0)CleanMemberMarkers();
                        }
                    });

                    for(int i=0;i<members.length();i++)
                    {
                        JSONObject member = members.getJSONObject(i);

                        int user_id = member.getInt("user_id");
                        String user_name = member.getString("user_name");
                        double user_latitude = member.getDouble("latitude");
                        double user_longitude = member.getDouble("longitude");

                        if(isInit)arrayList.add(new UserItem(user_id,user_name,false));

                        MapActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SetMemberMarker(user_latitude,user_longitude,user_name);

                            }
                        });

                    }

                    if(isInit){
                        MapActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    isInit = false;


                }catch (JSONException e)
                {

                }


            }

        });
        pusher.connect();
    }

    public void SetMemberMarker(double latitude,double longitude,String name)
    {
        Marker marker = new Marker();
        marker.setPosition(new LatLng(latitude,longitude));
        marker.setCaptionTextSize(15);
        marker.setIcon(MarkerIcons.BLACK);
        marker.setIconTintColor(GetColor(markerArrayList.size()-1));
        marker.setCaptionText(name);
        marker.setHideCollidedSymbols(true);

        if(latitude!=0&&longitude!=0&&!name.equals(UserData.shared.getName()))
        marker.setMap(map);

        markerArrayList.add(marker);
    }

    public int GetColor(int index)
    {
        index %= 6;
        switch (index)
        {
            case 0:
                return Color.RED;
            case 1:
                return getResources().getColor(R.color.mainColor1); //blue
            case 2:
                return Color.CYAN;

            case 3:
                return Color.YELLOW;
            case 4:
                return Color.MAGENTA;
            case 5:
                return Color.LTGRAY;
                default:
                    return Color.GREEN;
        }
    }

    public void CleanMemberMarkers()
    {
       for(int i=0;i< markerArrayList.size();i++)
       {
           markerArrayList.get(i).setMap(null);
       }
       markerArrayList.clear();
    }

    public void SetAppointmentMarker(double latitude,double longitude,String name)
    {
        appointMarker = new Marker();
        appointMarker.setPosition(new LatLng(latitude, longitude));
        appointMarker.setIcon(OverlayImage.fromResource(R.drawable.flag_icon));
        appointMarker.setCaptionText(name);
        appointMarker.setHideCollidedSymbols(true);
        appointMarker.setCaptionTextSize(16);
        appointMarker.setMap(map);

        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(latitude, longitude))
                .animate(CameraAnimation.Easing);
        map.moveCamera(cameraUpdate);

    }

    //약속 원 만들기  Clear
    public void SetCircle(double latitude,double longitude,double radius)
    {
        if(circle !=null) circle.setMap(null);
        circle = new CircleOverlay();
        circle.setCenter(new LatLng(latitude, longitude));
        circle.setOutlineColor(getResources().getColor(R.color.mainColor1));
        circle.setColor(Color.argb(35,69,79,161));
        circle.setOutlineWidth(3);
        circle.setRadius(radius);
        circle.setMap(map);
    }

    private Callback appoint=new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            MapActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MapActivity.this, "네트워크 문제로 잠시 뒤에 시도해주세요", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            String result = response.body().string();
            Log.d("Appointserver",result);

            try{
                JSONObject object = new JSONObject(result);

                if(object.getInt("result")==2000) //약속 실행 중
                {
                    arrayList =new ArrayList<>();
                    object = object.getJSONObject("data");

                    final String name = object.getString("name");
                    final String date = object.getString("date");
                    final String time = object.getString("date_time");
                    final double latitude =  object.getDouble("latitude");
                    final double longitude = object.getDouble("longitude");
                    final double radius = object.getDouble("radius");


                    MapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.map_appoint_time).setVisibility(View.VISIBLE); // 시간 보이게 하기
                            ((TextView)findViewById(R.id.map_add_btn)).setText("약속 상세보기");
                            ((TextView)findViewById(R.id.map_appoint_address)).setText(name);
                            if(appointMarker==null)
                                SetAppointmentMarker(latitude,longitude,name);
                            SetCircle(latitude,longitude,radius); //원 생성
                        }

                    });

                    JSONArray members = object.getJSONArray("members");

                    MapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(markerArrayList.size()>0)CleanMemberMarkers();
                        }
                    });

                    for(int i=0;i<members.length();i++) //홈화면으로 들어올시 게임 시작이면 멤버 바로 받아온다.
                    {
                        JSONObject member = members.getJSONObject(i);

                        int user_id = member.getInt("user_id");
                        String user_name = member.getString("user_name");
                        double user_latitude = member.getDouble("latitude");
                        double user_longitude = member.getDouble("longitude");

                        arrayList.add(new UserItem(user_id,user_name,false));

                        MapActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SetMemberMarker(user_latitude,user_longitude,user_name);
                            }
                        });

                    }

                    MapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Intent Service = new Intent(MapActivity.this, PromissService.class);
                            ContextCompat.startForegroundService(MapActivity.this, Service);
                        }
                    });
                    CalculateTime(date,time);
                    SetGameSetiing(object.getInt("id"));



                }else //약속 대기중 or 끝남
                {


                    object = object.getJSONObject("message");


                    if(object.getInt("status")==0) {
                        final String name = object.getString("name");
                        final String date = object.getString("date");
                        final String time = object.getString("date_time");
                        MapActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.map_appoint_time).setVisibility(View.VISIBLE); // 시간 보이게 하기
                                ((TextView) findViewById(R.id.map_add_btn)).setText("약속 상세보기");
                                ((TextView) findViewById(R.id.map_appoint_address)).setText(name);
                            }

                        });
                        CalculateTime(date, time);
                    }else //약속 끝남
                    {
                        BasicDB.setAppoint(getApplicationContext(),-1);
                    }
                }



            }catch (JSONException e)
            {
                e.printStackTrace();

                MapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MapActivity.this, "서버 문제로 잠시 뒤에 시도해주세요", Toast.LENGTH_SHORT).show();
                       BasicDB.setAppoint(getApplicationContext(),-1);
                      //  finish();
                    }
                });
            }


        }
    };

    public void CheckAppointment()
    {
        new Thread()
        {
            @Override
            public void run() {
                GetJson json = GetJson.getInstance();
                json.requestPost("api/Appointment/checkInvite",check,"id",UserData.shared.getId()+"");
            }
        }.run();
    }

    public void hideAcceptLayout(){
        findViewById(R.id.map_invite_name).setVisibility(View.GONE);
        findViewById(R.id.map_invite_accept).setVisibility(View.GONE);
        findViewById(R.id.map_invite_cancel).setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                acceptLayout.setVisibility(View.GONE);
            }
        },500);
    }

    public void showAcceptLayout(){
        String name =AppointmentData.data.getName();


        if(name !=null) {
            if (name.length() > 8) name = name.substring(0, 8);
            ((TextView) findViewById(R.id.map_invite_name)).setText(name + "에 초대되었습니다.");
        }
        acceptLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                findViewById(R.id.map_invite_name).setVisibility(View.VISIBLE);
                findViewById(R.id.map_invite_accept).setVisibility(View.VISIBLE);
                findViewById(R.id.map_invite_cancel).setVisibility(View.VISIBLE);

                findViewById(R.id.map_invite_accept).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new Thread()
                        {
                            @Override
                            public void run() {
                                GetJson json = GetJson.getInstance();
                                json.requestPost("api/Appointment/acceptInvite",accept,"id",UserData.shared.getId()+"","accept","1");
                            }
                        }.run();
                    }
                });


                findViewById(R.id.map_invite_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread()
                        {
                            @Override
                            public void run() {
                                GetJson json = GetJson.getInstance();
                                json.requestPost("api/Appointment/acceptInvite",accept,"id",UserData.shared.getId()+"","accept","0");
                            }
                        }.run();
                    }
                });



                findViewById(R.id.map_invite_cancel).setVisibility(View.VISIBLE);


                //추가하는 부분

            }
        },1000);
    }


    private Callback check=new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {


        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            String result = response.body().string();

            Log.d("Checkserver",result);
            try{

                JSONObject object = new JSONObject(result);

                if(object.getInt("result")==2000)
                {

                    object = object.getJSONObject("data");
                    AppointmentData data = AppointmentData.data;

                    data.setName(object.getString("name"));

                        MapActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAcceptLayout();
                            }
                        });
                }
            }catch (JSONException e)
            {

            }

        }
    }; // 초대 알람이 잇는 지 확인하는

    private  Callback accept = new Callback() { // 초대 알람 거부 수락 기능
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            MapActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideAcceptLayout();
                }
            });
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String result = response.body().string();
            Log.d("Acceptserver",result);

            try{
                MapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideAcceptLayout();
                    }
                });

                JSONObject object = new JSONObject(result);
                if(object.getInt("result")==2000)
                {
                    object = object.getJSONObject("data");
                    final int appoint_id = object.getInt("id");
                    BasicDB.setAppoint(getApplicationContext(),appoint_id);


                    final String name = object.getString("name");
                    final String date = object.getString("date");
                    final String time = object.getString("date_time");

                    MapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.map_appoint_time).setVisibility(View.VISIBLE); // 시간 보이게 하기
                            ((TextView)findViewById(R.id.map_add_btn)).setText("약속 상세보기");
                            ((TextView)findViewById(R.id.map_appoint_address)).setText(name);
                        }
                    });

                    CalculateTime(date,time);
                }else
                {

                }


            }catch (JSONException e)
            {

                MapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideAcceptLayout();
                    }
                });

            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if(map!=null)onMapReady(map);
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            GetAppointment();
        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map=naverMap;
        map.getUiSettings().setCompassEnabled(false);
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
    }

    class AppointTimer extends TimerTask {

        int hour;
        int minute;
        int second;
        String minute_S;
        String second_S;
        String hour_S;

        public AppointTimer(){
            this.minute=0; this.second=0;
            this.hour = 0;
        }
        public AppointTimer(int hour,int minute,int second)
        {
            this.hour=hour;this.minute=minute; this.second =second;
        }
        @Override
        public void run() {

            if(second==0) {

                if(minute==0)
                {
                    if(hour==0)
                    {
                        this.cancel();
                       // BasicDB.setAppoint(getApplicationContext(),-1);
                    }else
                    {
                        if(hour==2&&!isOnce){
                            isOnce=true;
                            this.cancel();
                            MapActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    GetAppointment();
                                }
                            });
                        }
                        hour--;
                        minute=59;
                        second=60;
                    }

                }else
                {
                    minute--;
                    second=60;
                }


            }else
            {
                second--;
            }
            MapActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(minute<10){
                        minute_S = "0"+minute;
                    }else
                    {
                        minute_S = minute+"";
                    }
                    if(second<10) {
                        second_S = "0" + second;
                    }else
                    {
                        second_S = ""+second;
                    }

                    if(hour<10)
                    {
                        hour_S = "0"+hour;
                    }else{
                        hour_S =""+hour;
                    }
                    time_textView.setText(hour_S+":"+minute_S+":"+second_S);
                }
            });

        }
    }

}
