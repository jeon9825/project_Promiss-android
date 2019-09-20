package com.skhu.cse.promiss;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.ferfalk.simplesearchview.SimpleSearchView;
import com.ferfalk.simplesearchview.utils.DimensUtils;
import com.google.gson.JsonObject;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.skhu.cse.promiss.Items.SearchAddressItem;
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

public class LocationSettingAcvtivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int EXTRA_REVEAL_CENTER_PADDING = 40;
    SimpleSearchView searchView;
    SearchAddressAdapter searchAddressAdapter;
    ArrayList<SearchAddressItem> arrayList = new ArrayList<>();
    RecyclerView recyclerView;
    NaverMap myMap;

    String shortName="";
    TextView address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_setting_acvtivity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = findViewById(R.id.searchView);


        address = findViewById(R.id.location_setting_address);

        searchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { //서치를 했을때
                Log.d("SimpleSearchView", "Submit:" + query);
                final String sQuery = query;

                if (sQuery.equals("")) return false;
                arrayList.clear();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        GetJson getJson = GetJson.getInstance();

                        LatLng lo=myMap.getCameraPosition().target;
                        Log.d("test",""+lo.longitude+","+lo.latitude);
                        getJson.requestMapApi(callback, sQuery,""+lo.longitude,""+lo.latitude);
                    }
                }.run();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SimpleSearchView", "Text changed:" + newText);
                return false;
            }

            @Override
            public boolean onQueryTextCleared() {
                Log.d("SimpleSearchView", "Text cleared");
                return false;
            }
        });
        recyclerView = findViewById(R.id.location_setting_search);
         recyclerView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAddressAdapter = new SearchAddressAdapter(this, arrayList);
        searchAddressAdapter.setClickEvent(new SearchAddressAdapter.clickEvent() {
            @Override
            public void onClick(View v, int position) {

                SearchAddressItem item = arrayList.get(position);

                double latitude =item.getLatitude();
                double longitude = item.getLongitude();

                shortName= item.getName();
                address.setText(item.getDetail());
                LatLng location = new LatLng(latitude,longitude);
                myMap.setCameraPosition(new CameraPosition(location,15));

                arrayList.clear();
                recyclerView.setVisibility(View.GONE);
            }
        });
        recyclerView.setAdapter(searchAddressAdapter);


        findViewById(R.id.map_setting_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();

                intent.putExtra("address",shortName);
                intent.putExtra("detail",address.getText().toString());

                LatLng location=myMap.getCameraPosition().target;
                intent.putExtra("latitude",location.latitude);
                intent.putExtra("longitude",location.longitude);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        searchAddressAdapter.notifyDataSetChanged();

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    public Callback callback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            LocationSettingAcvtivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LocationSettingAcvtivity.this, "네트워크 연결 실패", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String result = response.body().string();
            Log.d("server response:", result);
            try {
                JSONObject object = new JSONObject(result);

                if (object.getString("status").equals("OK")) { //성공일 경우
                    JSONArray array = object.getJSONArray("places");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject data = array.getJSONObject(i);
                        String jibunAddress = data.getString("jibun_address");
                        String title = data.getString("name");
                        arrayList.add(new SearchAddressItem(title, jibunAddress,data.getDouble("y"),data.getDouble("x")));
                    }
                    LocationSettingAcvtivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setVisibility(View.VISIBLE);
                            searchAddressAdapter.notifyDataSetChanged();
                        }
                    });
                } else { //실패했을 경우
                    LocationSettingAcvtivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LocationSettingAcvtivity.this, "서버 문제로 불러오기가 실패하였습니다", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                LocationSettingAcvtivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LocationSettingAcvtivity.this, "서버 문제로 불러오기가 실패하였습니다", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        setupSearchView(menu);
        return true;
    }

    private void setupSearchView(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);


        // Adding padding to the animation because of the hidden menu item
        Point revealCenter = searchView.getRevealAnimationCenter();
        revealCenter.x -= DimensUtils.convertDpToPx(EXTRA_REVEAL_CENTER_PADDING, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (searchView.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (searchView.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        myMap = naverMap;
    }
}
