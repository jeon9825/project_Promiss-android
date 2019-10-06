package com.skhu.cse.promiss.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.skhu.cse.promiss.AddAppointmentActivity;
import com.skhu.cse.promiss.MainActivity;
import com.skhu.cse.promiss.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class Add_Appointment_Fragment_3 extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    TextView date;
    TextView time;
    Fragment main;
    Calendar now;
    int appoint_hour;
    int appoint_minute;
    String hourOfDay;
    String date_S;
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


        now = Calendar.getInstance();

        int year=now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH)+1;
        int day = now.get(Calendar.DAY_OF_MONTH);

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        date=view.findViewById(R.id.frg_appointment_3_t5);
        time=view.findViewById(R.id.frg_appointment_3_t6);
        date_S = ""+year+"-"+month+"-"+day;

        date.setText(date_S);
        if(hour<=21&&hour>=0||hour==21&&minute==0)
        appoint_hour = hour+3;
        else if(hour >21)
        {
            day++;
            date_S = ""+year+"-"+month+"-"+day;
            date.setText(date_S);
        }

        String time_S;
        if(appoint_hour>12)
            time_S="오후 "+(appoint_hour-12);
        else if(appoint_hour==12)
            time_S="오후 "+appoint_hour;
        else
        {
           if(appoint_hour<10)
               time_S="오전 0"+appoint_hour;
           else
               time_S="오전 "+appoint_hour;
        }


        appoint_minute = minute;
        if(minute<10)
            time_S = time_S + ":0"+minute;
        else
        time_S = time_S + ":"+minute;
        hourOfDay= appoint_hour+":"+minute;
        time.setText(time_S);


        main=this;


        date.setOnClickListener(view1 -> {

            if(getFragmentManager().findFragmentByTag("Datepickerdialog")==null) {

                now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        (DatePickerDialog.OnDateSetListener) main,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
                dpd.setAccentColor(getResources().getColor(R.color.mainColor1));
// If you're calling this from a support Fragment
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        time.setOnClickListener(view1->{

            if(getFragmentManager().findFragmentByTag("Timepickerdialog")==null) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog dpd = TimePickerDialog.newInstance(
                        (TimePickerDialog.OnTimeSetListener) main,
                        now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false
                );
// If you're calling this from a support Fragment
                dpd.setAccentColor(getResources().getColor(R.color.mainColor1));
                dpd.show(getFragmentManager(), "Timepickerdialog");

            }
        });
        view.findViewById(R.id.frg_appointment_3_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //넘어가는 부분
                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.HOUR_OF_DAY,appoint_hour);
                String[] date_array= date.getText().toString().split("-");
                String[] time_S = hourOfDay.split(":");


                GregorianCalendar now = new GregorianCalendar(temp.get(Calendar.YEAR),
                        temp.get(Calendar.MONTH)+1,temp.get(Calendar.DAY_OF_MONTH),
                        temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.MINUTE));

                GregorianCalendar appoint = new GregorianCalendar(Integer.parseInt(date_array[0]),
                        Integer.parseInt(date_array[1]),Integer.parseInt(date_array[2]),
                        Integer.parseInt(time_S[0]),Integer.parseInt(time_S[1]));

                long diff=appoint.getTimeInMillis()-now.getTimeInMillis();

                if(diff>0) {
                    ((AddAppointmentActivity) getActivity()).setAppointment_Date(date_S, hourOfDay);
                    ((AddAppointmentActivity) getActivity()).Next();
                }else
                {
                    Toast.makeText(getActivity(),"현재 시간보다 3시간 이후로\n 설정해주세요",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String temp ;

        temp=""+year+"-"+(monthOfYear+1)+"-"+dayOfMonth;

        date_S = ""+year+"-";

        if(monthOfYear+1>10)
            date_S += (monthOfYear+1)+"-";
        else
            date_S += "0"+(monthOfYear+1)+"-";



        if(dayOfMonth>10)
            date_S += (dayOfMonth);
        else
            date_S += "0"+(dayOfMonth)+"-";

        date.setText(date_S);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time_S ;


        this.hourOfDay=hourOfDay+":"+minute;
        if(hourOfDay>12){
            if(hourOfDay-12<10)
                time_S="오후 0"+(hourOfDay-12);
                else
            time_S="오후 "+(hourOfDay-12);
        }
        else if(hourOfDay==12)
            time_S="오후 "+hourOfDay;
        else{
            if(hourOfDay<10)
                time_S="오전 0"+hourOfDay;
            else
                time_S="오전 "+hourOfDay;
        }


        if(minute<10)
        time_S+=":0"+minute;
        else
            time_S+=":"+minute;
        time.setText(time_S);
    }
}
