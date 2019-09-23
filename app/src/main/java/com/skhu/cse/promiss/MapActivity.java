package com.skhu.cse.promiss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.skhu.cse.promiss.database.BasicDB;
import com.skhu.cse.promiss.server.GetJson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    NaverMap map;
    RelativeLayout acceptLayout;

    boolean isAppointment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

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
                    startActivity(intent);
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
            Log.d("server",result);

            try{
                JSONObject object = new JSONObject(result);

                if(object.getInt("result")==2000) //약속 실행 중
                {
                    object = object.getJSONObject("data");
                    ((TextView)findViewById(R.id.map_add_btn)).setText("약속 상세보기");
                    ((TextView)findViewById(R.id.map_appoint_address)).setText(object.getString("data"));

                }else //약속 대기중
                {
                    findViewById(R.id.map_appoint_time).setVisibility(View.VISIBLE); // 시간 보이게 하기
                    object = object.getJSONObject("data");
                    ((TextView)findViewById(R.id.map_add_btn)).setText("약속 상세보기");
                    ((TextView)findViewById(R.id.map_appoint_address)).setText(object.getString("data"));

                }



            }catch (JSONException e)
            {
                MapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MapActivity.this, "네트워크 문제로 잠시 뒤에 시도해주세요", Toast.LENGTH_SHORT).show();
                        finish();
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
        acceptLayout.setVisibility(View.VISIBLE);
        String name =AppointmentData.data.getName();


        if(name !=null) {
            if (name.length() > 8) name = name.substring(0, 8);
            ((TextView) findViewById(R.id.map_invite_name)).setText(name + "에 초대되었습니다.");
        }
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

            Log.d("server",result);
            try{

                JSONObject object = new JSONObject(result);

                if(object.getInt("result")==2000)
                {

                    object = object.getJSONObject("data");
                    AppointmentData data = AppointmentData.data;

                    data.setName(object.getString("address"));

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
    };

    private  Callback accept = new Callback() {
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
            Log.d("server",result);

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
    public void onMapReady(@NonNull NaverMap naverMap) {
        map=naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
    }
}
