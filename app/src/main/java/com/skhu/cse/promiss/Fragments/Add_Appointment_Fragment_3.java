package com.skhu.cse.promiss.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skhu.cse.promiss.AddAppointmentActivity;
import com.skhu.cse.promiss.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Add_Appointment_Fragment_3 extends Fragment {


    public Add_Appointment_Fragment_3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add__appointment__fragment_3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.frg_appointment_3_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //넘어가는 부분
                ((AddAppointmentActivity)getActivity()).Next();
            }
        });
    }
}
