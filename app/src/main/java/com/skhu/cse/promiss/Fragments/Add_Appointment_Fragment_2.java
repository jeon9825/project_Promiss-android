package com.skhu.cse.promiss.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.naver.maps.map.MapFragment;
import com.skhu.cse.promiss.AddAppointmentActivity;
import com.skhu.cse.promiss.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Add_Appointment_Fragment_2 extends Fragment {

    TextView address;//

    EditText address_detail;//상세 주소 입력한 것

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


        view.findViewById(R.id.frg_appointment_2_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AddAppointmentActivity)getActivity()).Next();
            }
        });
    }
}
