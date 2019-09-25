package com.skhu.cse.promiss.Fragments;


import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skhu.cse.promiss.AddAppointmentActivity;
import com.skhu.cse.promiss.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class Add_Appointment_Fragment_4 extends Fragment  {


    ImageButton add_1;
    ImageButton add_2;
    ImageButton minus_1;
    ImageButton minus_2;

    TextView money_cycle;
    TextView money;

    int money_cycle_N=5;

    int money_N=100;



    public Add_Appointment_Fragment_4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add__appointment__fragment_4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        add_1 = view.findViewById(R.id.frg_appointment_4_add);
        add_2 = view.findViewById(R.id.frg_appointment_4_add_2);
        minus_1 = view.findViewById(R.id.frg_appointment_4_minus);
        minus_2 = view.findViewById(R.id.frg_appointment_4_minus_2);

        money = view.findViewById(R.id.frg_appointment_4_money);
        money_cycle=view.findViewById(R.id.frg_appointment_4_money_cycle);



        View.OnClickListener addMoney=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.frg_appointment_4_add_2) //벌금 금액
                {
                    if(money_N==10000) //최대 금액
                    {
                        Toast.makeText(getActivity(),"최소 금액입니다.",Toast.LENGTH_LONG).show();
                    }else
                    {
                        money_N +=100;
                        money.setText(""+money_N);
                    }
                }else
                {
                    if(money_cycle_N==60)//최대 주기
                    {
                        Toast.makeText(getActivity(),"최대 주기입니다.",Toast.LENGTH_LONG).show();
                    }else
                    {
                        money_cycle_N+=5;
                        money_cycle.setText(""+money_cycle_N);
                    }

                }

            }
        };

        View.OnClickListener minusMoney=new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(view.getId()==R.id.frg_appointment_4_minus_2) //벌금 주기
                {
                    if(money_N==100) //최소 금액
                    {
                        Toast.makeText(getActivity(),"최소 금액입니다.",Toast.LENGTH_LONG).show();
                    }else
                    {
                        money_N-=100;
                        money.setText(""+money_N);
                    }
                }else
                {
                    if(money_cycle_N==5)//최소 주기
                    {
                        Toast.makeText(getActivity(),"최소 주기입니다.",Toast.LENGTH_LONG).show();
                    }else
                    {
                        money_cycle_N-=5;
                        money_cycle.setText(""+money_cycle_N);
                    }

                }

            }
        };

        add_1.setOnClickListener(addMoney);
        add_2.setOnClickListener(addMoney);
        minus_1.setOnClickListener(minusMoney);
        minus_2.setOnClickListener(minusMoney);




        view.findViewById(R.id.frg_appointment_4_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AddAppointmentActivity)getActivity()).setAppointment_Money(money_cycle_N,money_N);
                ((AddAppointmentActivity)getActivity()).Next();

            }
        });
    }


}
