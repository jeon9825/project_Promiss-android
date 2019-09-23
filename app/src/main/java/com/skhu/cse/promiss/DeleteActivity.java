package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.skhu.cse.promiss.custom.PromissDialog;
import com.skhu.cse.promiss.database.BasicDB;

public class DeleteActivity extends AppCompatActivity {

    RelativeLayout after_delete;
    RelativeLayout delete_default;

    EditText password;
    PromissDialog builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        after_delete= findViewById(R.id.delete_success);
        delete_default=findViewById(R.id.delete_default_body);

        password=findViewById(R.id.delete_editText_check);

        findViewById(R.id.delete_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.delete_default_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               builder=new PromissDialog.Builder(DeleteActivity.this)
                        .setTitle("회원 탈퇴")
                        .setMessage("정말로 탈퇴하시겠습니까?")
                        .addCancelListener("취소", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                builder.dismiss();
                            }
                        }).addOkayListener("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UserDeleteSuccess();
                                BasicDB.setId(getApplicationContext(),-1);
                                builder.dismiss();
                            }
                        }).build();
               builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //배경 뒤에 안보이기
               builder.show();

            }
        });
    }

    public void UserDeleteSuccess(){
        delete_default.setVisibility(View.GONE);
        after_delete.setVisibility(View.VISIBLE);

        //로그인 버튼 클릭시 로그인 화면만 남겨두고 나머지 엑티비티 종료


    }
}
