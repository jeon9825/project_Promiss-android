package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ChangePassword extends AppCompatActivity {


    EditText editText_pw;
    EditText editText_pw_check;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editText_pw=findViewById(R.id.change_password_edit_pw);
        editText_pw_check  = findViewById(R.id.change_password_edit_pw_ok);
        message = findViewById(R.id.change_password_check_pw);

        
        findViewById(R.id.change_password_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
