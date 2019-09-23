package com.skhu.cse.promiss.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.skhu.cse.promiss.AddAppointmentActivity;
import com.skhu.cse.promiss.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Add_Appointment_Fragment_1 extends Fragment {


    EditText appointment_name;
    Button checkBtn;


    public Add_Appointment_Fragment_1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add__appointment__fragment_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appointment_name=view.findViewById(R.id.frg_appointment_1_editText_check);
        checkBtn=view.findViewById(R.id.frg_appointment_1_btn);


        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = appointment_name.getText().toString();

                if(result.equals(""))
                {
                    Toast.makeText(getActivity(),"이름을 입력해주세요",Toast.LENGTH_LONG).show();
                }else
                {
                    //약속 이름 저장
                    ((AddAppointmentActivity)getActivity()).setAppointmentName(result);
                    //넘어가는 부분
                    ((AddAppointmentActivity)getActivity()).Next();

                }
            }
        });
    }


}
