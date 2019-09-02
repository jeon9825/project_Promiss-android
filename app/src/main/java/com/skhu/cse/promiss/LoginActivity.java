package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText editText_id ;
    EditText editText_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_id=(EditText)findViewById(R.id.login_edit_id);
        editText_password=(EditText)findViewById(R.id.login_edit_password);
        Button b = (Button)findViewById(R.id.login_button);
        Intent i = getIntent();
        String id;
        String pw;
        try {
            id= i.getStringExtra("id");
            editText_id.setText(id);
            pw= i.getStringExtra("pw");
            editText_id.setText(pw);
        }catch (NullPointerException e){
            Toast.makeText(LoginActivity.this, "아이디나 비번 입력하라구",Toast.LENGTH_LONG).show();
        }
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = editText_id.getText().toString();
                String password = editText_password.getText().toString();
                Toast.makeText(LoginActivity.this,"id는"+id+" 비밀번호는 "+password,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
