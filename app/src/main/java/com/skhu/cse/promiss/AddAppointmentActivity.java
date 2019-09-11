package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.skhu.cse.promiss.custom.PromissDialog;

public class AddAppointmentActivity extends AppCompatActivity {

    PromissDialog builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_apointment);


        findViewById(R.id.add_appointment_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               builder =new PromissDialog.Builder(AddAppointmentActivity.this)
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
            }
        });
    }
}
