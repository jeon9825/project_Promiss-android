package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class DeleteActivity extends AppCompatActivity {

    RelativeLayout after_delete;
    RelativeLayout delete_default;

    EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        after_delete= findViewById(R.id.delete_success);
        delete_default=findViewById(R.id.delete_default_body);

        password=findViewById(R.id.delete_editText_check);
    }

    public void UserDeleteSuccess(){
        delete_default.setVisibility(View.GONE);
        after_delete.setVisibility(View.VISIBLE);

        //로그인 버튼 클릭시 모든


    }
}
