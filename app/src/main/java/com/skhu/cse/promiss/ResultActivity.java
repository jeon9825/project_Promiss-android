package com.skhu.cse.promiss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.MarkerIcons;
import com.skhu.cse.promiss.Items.UserData;
import com.skhu.cse.promiss.Items.UserItem;
import com.skhu.cse.promiss.custom.PromissDialog;
import com.skhu.cse.promiss.database.BasicDB;
import com.skhu.cse.promiss.server.GetJson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity implements OnMapReadyCallback {

    NaverMap map;
    TextView nameTV;
    TextView timeTV;
    TextView FineTime;
    InfoWindow infoWindow;
    RecyclerView recyclerView;
    PromissDialog builder;
    int current_position;
    double appoint_latitude;
    int fine;
    double appoint_longitude;
    CircleOverlay circle; //원 자기장
    PolylineOverlay polyline = new PolylineOverlay();
    ArrayList<LatLng> position = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<UserItem> arrayList=new ArrayList<>();
    ArrayList<Double> radius = new ArrayList<>();
    ResultMemberListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        current_position=0;
        recyclerView = findViewById(R.id.result_recycler);
        nameTV = findViewById(R.id.result_name);
        timeTV = findViewById(R.id.result_time);

        appoint_latitude=0;
        appoint_longitude=0;

        FineTime = findViewById(R.id.result_Fine_time);
        findViewById(R.id.result_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new PromissDialog.Builder(ResultActivity.this)
                        .setTitle("약속 결과")
                        .setMessage("나가시면 이 정보를 다시 볼 수 없습니다. \n정말로 나가시겠습니까?")
                        .addCancelListener("취소", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                builder.dismiss();
                            }
                        }).addOkayListener("나가기", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                builder.dismiss();
                                BasicDB.setPREF_Result(getApplicationContext(),-1);
                                finish();
                            }
                        }).build();
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //배경 뒤에 안보이기
                builder.show();
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;

        infoWindow = new InfoWindow();
        map.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                infoWindow.close();
            }
        });
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return (CharSequence)infoWindow.getMarker().getTag();
            }
        });
        adapter = new ResultMemberListAdapter(this,arrayList);
        adapter.setEvent(new ResultMemberListAdapter.ClickEvent() {
            @Override
            public void afterClick(View view, int position) {

                Clearinit();

                if(current_position!=position) {
                    current_position=position;
                    new Thread() {
                        @Override
                        public void run() {
                            GetJson json = GetJson.getInstance();
                            UserItem item = arrayList.get(position);
                            json.requestPost("api/Appointment/myResult", result, "appoint_id", "" + BasicDB.getResult(getApplicationContext()), "id", item.getId() + "");
                        }
                    }.run();
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        new Thread(){
            @Override
            public void run() {
                GetJson json =  GetJson.getInstance();
                json.requestPost("api/Appointment/Results",results,"appoint_id",""+BasicDB.getResult(getApplicationContext()),"id", UserData.shared.getId()+"");
            }
        }.run();

    }

    private Callback result = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            ResultActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ResultActivity.this,"네트워크 문제로 정보를 불러올 수 없습니다.",Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            String s = response.body().string();
            Log.d("result",s);
            try{

                JSONObject object = new JSONObject(s);
                setPolylineMarkers(object.getJSONArray("data"));
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    };


    public void Clearinit()
    {
        for(Marker marker : markers)
        {
            marker.setMap(null);
        }
        markers.clear();
        position.clear();
        radius.clear();
        polyline.setMap(null);
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

//        CameraUpdate cameraUpdate = CameraUpdate.fitBounds(polyline.getBounds());
//        map.moveCamera(cameraUpdate);
    }
    public int GetColor(int index)
    {
        index %= 4;
        switch (index)
        {
            case 0:
                return getResources().getColor(R.color.member1);
            case 1:
                return getResources().getColor(R.color.member2); //blue
            case 2:
                return getResources().getColor(R.color.member3);

            default:
                return getResources().getColor(R.color.same);

        }
    }

    private Callback results = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            ResultActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ResultActivity.this,"네트워크 문제로 정보를 불러올 수 없습니다.",Toast.LENGTH_LONG).show();
                    finish();

                }
            });
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            String s = response.body().string();
            Log.d("result",s);
            try{
                JSONObject object = new JSONObject(s);

                object = object.getJSONObject("data");
                JSONObject appoint = object.getJSONObject("appoint");

                String name = appoint.getString("name");
                int fine_time= appoint.getInt("Fine_time");
                fine = appoint.getInt("Fine_money");
                String date = appoint.getString("date");
                String time = appoint.getString("date_time");

                appoint_latitude= appoint.getDouble("latitude");
                appoint_longitude = appoint.getDouble("longitude");

                ResultActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nameTV.setText(name);

                        CameraUpdate update = CameraUpdate.scrollTo(new LatLng(appoint_latitude,appoint_longitude));

                        map.moveCamera(update);
                        String[] dates = date.split("-");
                        timeTV.setText(dates[0]+"년"+dates[1]+"월"+dates[2]+"일 "+time.substring(0,5));
                        FineTime.setText("("+fine+"원/"+fine_time+"분)");
                    }
                });

                JSONArray members = object.getJSONArray("member");


                for(int i=0;i<members.length();i++)
                {
                    JSONObject member = members.getJSONObject(i);

                    int user_id = member.getInt("user_id");
                    String user_name = member.getString("user_name");
                    int member_fine = member.getInt("Fine_final");


                    if(user_id==UserData.shared.getId()) //벌금 가져오기
                    {
                       arrayList.add(0,new UserItem(user_id,user_name,member_fine));
                    }else
                    {
                        arrayList.add(new UserItem(user_id,user_name,member_fine));
                    }
                    ResultActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                JSONArray results = object.getJSONArray("result");


                setPolylineMarkers(results);

            }catch (JSONException e)
            {
                ResultActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ResultActivity.this,"서버 문제로 정보를 불러올 수 없습니다.",Toast.LENGTH_LONG).show();
                        finish();
                        BasicDB.setPREF_Result(getApplicationContext(),-1);
                    }
                });
                e.printStackTrace();
            }
        }
    };

    public void setPolylineMarkers(JSONArray results)
    {
        try {

            if(results.length()==0)
            {
                ResultActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ResultActivity.this,"실행되지 않았던 약속입니다.",Toast.LENGTH_LONG).show();
                        finish();
                        BasicDB.setPREF_Result(getApplicationContext(),-1);
                    }
                });
            }

            for (int i = 0; i < results.length(); i++) {
                JSONObject member = results.getJSONObject(i);
                double user_latitude = member.getDouble("latitude");
                double user_longitude = member.getDouble("longitude");
                int Fine = member.getInt("Fine_current");
                double appoint_radius = member.getDouble("appointment_radius");
                position.add(new LatLng(user_latitude, user_longitude));
                radius.add(appoint_radius);
                int circle_in = member.getInt("circle_in");

                String date_time = member.getString("time");

                Marker marker = new Marker();
                marker.setPosition(new LatLng(user_latitude, user_longitude));
                String tag = "누적벌금: " + Fine + "원";

                if (circle_in == 1) tag = "[IN]0원부과\n" + tag;
                else {
                    tag = "[OUT] " + fine + "원부과\n" + tag;
                }

                tag = date_time.substring(0, 5) + "\n" + tag;


                marker.setTag(tag);
                marker.setIcon(MarkerIcons.BLACK);
                marker.setIconTintColor(GetColor(arrayList.size()-1));
                marker.setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay) {

                        int position = markers.indexOf((Marker) overlay);
                        SetCircle(appoint_latitude, appoint_longitude, radius.get(position));
                        infoWindow.open((Marker) overlay);
                        return true;
                    }
                });
                markers.add(marker);
            }
            ResultActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    polyline.setCoords(position);
                    polyline.setColor(getResources().getColor(R.color.mainColor1));
                    polyline.setMap(map);
                    for (Marker marker : markers) {
                        marker.setMap(map);
                    }
                }
            });
        }catch (JSONException e)
        {
            e.printStackTrace();
            ResultActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ResultActivity.this,"서버 문제로 정보를 불러올 수 없습니다.",Toast.LENGTH_LONG).show();
                    BasicDB.setPREF_Result(getApplicationContext(),-1);
                    finish();
                }
            });
        }
    }

}
