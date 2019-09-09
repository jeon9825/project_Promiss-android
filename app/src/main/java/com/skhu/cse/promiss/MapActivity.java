package com.skhu.cse.promiss;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        final String[] menu={"로그아웃","비밀번호 변경","회원 탈퇴"};

        //setting 버튼 클릭
        findViewById(R.id.map_person_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MapActivity.this)
                        .setTitle("설정")
                        .setItems(menu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MapActivity.this,i+"번째 클릭",Toast.LENGTH_LONG).show();

                                Intent intent=new Intent(MapActivity.this,DeleteActivity.class);
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        });
    }
}
