package com.skhu.cse.promiss;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.skhu.cse.promiss.Fragments.Add_Appointment_Fragment_1;
import com.skhu.cse.promiss.Fragments.Add_Appointment_Fragment_2;
import com.skhu.cse.promiss.Fragments.Add_Appointment_Fragment_3;
import com.skhu.cse.promiss.Fragments.Add_Appointment_Fragment_4;
import com.skhu.cse.promiss.Fragments.Add_Appointment_Fragment_5;
import com.skhu.cse.promiss.Fragments.Add_Appointment_Fragment_6;
import com.skhu.cse.promiss.Items.AppointmentItem;
import com.skhu.cse.promiss.Items.UserData;
import com.skhu.cse.promiss.custom.PromissDialog;
import com.skhu.cse.promiss.server.GetJson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddAppointmentActivity extends AppCompatActivity {

    PromissDialog builder;
    ImageButton back_btn;
    AppointmentItem item=AppointmentItem.item;

    public static final int map_setting=1001;
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

    public void setAppointment_Date(String date,String time)
    {
        item.setDate(date);
        item.setTime(time);
    }

    public void setAppointment_user(ArrayList<Integer> users)
    {
        item.setMember_num(users.size());
        item.setIntegers(users);
    }

    public void setAppointment_Money(int money_cycle,int money)
    {
        item.setMoney(money);
        item.setMoney_cycle(money_cycle);
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
        }else //약속 생송
        {

            //약속 생성
            new Thread(){
                @Override
                public void run() {
                    GetJson json=GetJson.getInstance();

                    String[] body = new String[]{
                            "id", UserData.shared.getId()+"",
                            "address",item.getAddress(),
                            "detail",item.getAddress_detail(),
                            "latitude",item.getLatitude()+"",
                            "longitude",item.getLongitude()+"",
                            "date",item.getDate(),
                            "date_time",item.getTime(),
                            "Fine_time",item.getMoney_cycle()+"",
                            "Fine_money", item.getMoney()+"",
                            "num",item.getIntegers().size()+""
                    };

                    ArrayList<String> body_arrayList = new ArrayList<>();
                    body_arrayList.addAll(Arrays.asList(body));


                    ArrayList<Integer> integers = item.getIntegers();

                    for(int i=0;i<integers.size();i++)
                    {
                        body_arrayList.add("member_id"+i);
                        body_arrayList.add(integers.get(i).toString());
                    }

                    json.requestPost("api/Appointment/newAppointment",callback,body_arrayList.toArray(new String[]{}));

                }
            }.run();
            finish();
        }
    }


    Callback callback =new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            AddAppointmentActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddAppointmentActivity.this, "현재 네트워크 문제로 약속을 생성할 수 없습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String result = response.body().string();
            Log.d("result",result);
            try{
                JSONObject object = new JSONObject(result);

                if(object.getInt("result")==2000)
                {

                    AddAppointmentActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddAppointmentActivity.this, "약속 생성에 성공하였습니다.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });

                }else
                {
                    final String message = object.getString("message");
                    AddAppointmentActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddAppointmentActivity.this, message, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }


            }catch (JSONException e)
            {
                AddAppointmentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddAppointmentActivity.this, "현재 서버 문제로 약속을 생성할 수 없습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        }
    };


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {

           if(data!=null) {
               Log.d("test","test");
               String address = data.getStringExtra("address");
               String detail = data.getStringExtra("detail");
               double latitude = data.getDoubleExtra("latitude", 0.0);
               double longitude = data.getDoubleExtra("longitude", 0.0);

               add_appointment_fragment_2.mapSetting(address,detail,latitude,longitude);

           }
        }
    }
}
