package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText editText_ID;
    EditText editText_PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    editText_ID=findViewById(R.id.register_edit_id);
    editText_PW=findViewById(R.id.register_edit_password);
    Button button_OK = findViewById(R.id.register_button_ok);
    button_OK.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String id = editText_ID.getText().toString();
            String pw = editText_PW.getText().toString();
            Toast.makeText(RegisterActivity.this,"id는"+id+" 비밀번호는 "+pw,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("pw", pw);
            startActivity(intent);
            finish();
        }
    });
    }
}
