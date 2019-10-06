package com.skhu.cse.promiss.Fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.MarkerIcons;
import com.skhu.cse.promiss.AddAppointmentActivity;
import com.skhu.cse.promiss.LocationSettingAcvtivity;
import com.skhu.cse.promiss.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Add_Appointment_Fragment_2 extends Fragment implements OnMapReadyCallback {

    TextView address;//

    EditText address_detail;//상세 주소 입력한 것

    NaverMap naverMap;
    Marker marker;
    boolean map_Check=false;

    public Add_Appointment_Fragment_2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add__appointment__fragment_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        address=view.findViewById(R.id.frg_appointment_2_address);

        address_detail=view.findViewById(R.id.frg_appointment_2_address_detail);

        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);



        view.findViewById(R.id.frg_appointment_2_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(map_Check) {

                    double latitude;
                    double longitude;

                    String address_string; // 주소
                    String address_detail_string; // 상세주소

                    if (naverMap != null) {
                        LatLng location = naverMap.getCameraPosition().target;

                        latitude = location.latitude;
                        longitude = location.longitude;

                        address_string = address.getText().toString();
                        address_detail_string = address_detail.getText().toString();
                        ((AddAppointmentActivity) getActivity()).setAppointmentAddress(latitude, longitude, address_string, address_detail_string);
                        ((AddAppointmentActivity) getActivity()).Next();
                    } else {
                        Toast.makeText(getActivity(), "시스템 에러로 다시 시도해 주십시오", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                }else
                {
                    Toast.makeText(getActivity(), "지도를 클릭하여 장소를 설정해주세요", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void mapSetting(String address,String detail,double latitude,double longitude)
    {

        LatLng latLng = new LatLng(latitude,longitude);
        naverMap.setCameraPosition(new CameraPosition(latLng,15));

        if(marker !=null)
        marker.setMap(null);
        marker = new Marker();
        marker.setCaptionText(address);
        marker.setSubCaptionText(detail);
        marker.setIcon(MarkerIcons.BLACK);
        marker.setIconTintColor(getResources().getColor(R.color.mainColor1));
        marker.setPosition(latLng);
        marker.setMap(naverMap);
        this.address.setText(address);
        this.address_detail.setText(detail);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap=naverMap;

        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                map_Check=true;
                Intent intent=new Intent(getActivity(), LocationSettingAcvtivity.class);
                startActivityForResult(intent,AddAppointmentActivity.map_setting);
            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}
