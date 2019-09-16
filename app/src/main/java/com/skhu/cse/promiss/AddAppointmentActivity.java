package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.skhu.cse.promiss.Fragments.Add_Appointment_Fragment_1;
import com.skhu.cse.promiss.Fragments.Add_Appointment_Fragment_2;
import com.skhu.cse.promiss.Fragments.Add_Appointment_Fragment_3;
import com.skhu.cse.promiss.Fragments.Add_Appointment_Fragment_4;
import com.skhu.cse.promiss.Fragments.Add_Appointment_Fragment_5;
import com.skhu.cse.promiss.Fragments.Add_Appointment_Fragment_6;
import com.skhu.cse.promiss.Items.AppointmentItem;
import com.skhu.cse.promiss.custom.PromissDialog;

public class AddAppointmentActivity extends AppCompatActivity {

    PromissDialog builder;
    ImageButton back_btn;
    AppointmentItem item=AppointmentItem.item;

    int index=1; //보고있는 화면 인덱스
    int maxIndex=6; //마지막 화면 인덱스

    Add_Appointment_Fragment_1 add_appointment_fragment_1;
    Add_Appointment_Fragment_2 add_appointment_fragment_2;
    Add_Appointment_Fragment_3 add_appointment_fragment_3;
    Add_Appointment_Fragment_4 add_appointment_fragment_4;
    Add_Appointment_Fragment_5 add_appointment_fragment_5;
    Add_Appointment_Fragment_6 add_appointment_fragment_6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_apointment);


        back_btn=findViewById(R.id.add_appointment_back);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back();
            }
        });
        findViewById(R.id.add_appointment_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(index !=6) {
                    builder = new PromissDialog.Builder(AddAppointmentActivity.this)
                            .setTitle("약속 만들기 취소")
                            .setMessage("약속 정보가 저장되지 않습니다.\n정말로 취소하시겠습니까?")
                            .addCancelListener("계속 만들기", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    builder.dismiss();
                                }
                            }).addOkayListener("만들기 취소", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    builder.dismiss();
                                    finish();
                                }
                            }).build();
                    builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //배경 뒤에 안보이기
                    builder.show();
                }else //약속 만들기 완료
                {
                    finish();
                }
            }
        });

        add_appointment_fragment_1=new Add_Appointment_Fragment_1();

        ShowFrgament(add_appointment_fragment_1);
    }


    public void setAppointmentName(String name)
    {
        item.setName(name);
    }

    public void setAppointmentAddress(double latitude,double longitude,String addres,String deteil)
    {
        item.setLatitude(latitude);
        item.setLongitude(longitude);
        item.setAddress(addres);
        item.setAddress_detail(deteil);
    }


    public void Back(){
        if(index!=1){ //더이상 뒤로갈 수 없다.

            Fragment fragment= GetFragment(--index);

            if(index==1) //뒤로가기 UI 지우기
            {
                back_btn.setVisibility(View.GONE);
            }
            ShowFrgament(fragment);
        }else
        {
            finish();
        }
    }

    public void Next(){
        if(index!=maxIndex){ //더이상 뒤로갈 수 없다.

            Fragment fragment= GetFragment(++index);
            if(index==2) //뒤로가기 UI 만들기
            {
                back_btn.setVisibility(View.VISIBLE);
            }
            ShowFrgament(fragment);
        }
    }



    public Fragment GetFragment(int index)
    {
        Fragment fragment=null;

        switch (index)
        {
            case 1:

                if(add_appointment_fragment_1==null)add_appointment_fragment_1=new Add_Appointment_Fragment_1();
                fragment=add_appointment_fragment_1;
                break;
            case 2:
                if(add_appointment_fragment_2==null)add_appointment_fragment_2=new Add_Appointment_Fragment_2();
                fragment=add_appointment_fragment_2;
                break;
            case 3:
                if(add_appointment_fragment_3==null)add_appointment_fragment_3=new Add_Appointment_Fragment_3();
                fragment=add_appointment_fragment_3;
                break;
            case 4:
                if(add_appointment_fragment_4==null)add_appointment_fragment_4=new Add_Appointment_Fragment_4();
                fragment=add_appointment_fragment_4;
                break;
            case 5:
                if(add_appointment_fragment_5==null)add_appointment_fragment_5=new Add_Appointment_Fragment_5();
                fragment=add_appointment_fragment_5;
                break;
            case 6:
                if(add_appointment_fragment_6==null)add_appointment_fragment_6=new Add_Appointment_Fragment_6();
                fragment=add_appointment_fragment_6;
                break;
        }

        return fragment;
    }

    public void ShowFrgament(Fragment frgmaent)
    {
        if(frgmaent!=null)
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frgment,frgmaent).commit();
    }

    @Override
    public void onBackPressed() {
        Back();
    }
}
