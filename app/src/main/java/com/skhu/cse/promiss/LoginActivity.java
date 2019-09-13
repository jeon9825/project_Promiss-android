package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skhu.cse.promiss.keyboard.SoftKeyboard;
import com.skhu.cse.promiss.server.GetJson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText editText_id ;
    EditText editText_password;
    InputMethodManager inputManager;
    RelativeLayout loginView;
    View center_view;
    SoftKeyboard softKeyboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        center_view = findViewById(R.id.login_center_view);
        inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        loginView=findViewById(R.id.login);
        softKeyboard =new SoftKeyboard(loginView,inputManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                Log.d("키보드","내려감");
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        center_view.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                Log.d("키보드","올라감");
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        center_view.setVisibility(View.GONE);
                    }
                });
            }
        });
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

                final String id = editText_id.getText().toString();
                final String password = editText_password.getText().toString();
//                new Thread(){
//                    @Override
//                    public void run() {
//                        super.run();
//                        GetJson getJson = GetJson.getInstance();
//                        getJson.requestPost("api/User/Login",callback,"id",id,"pw",password);
//                    }
//                }.start();
                Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) { // 통신 실패
            Toast.makeText(LoginActivity.this,"로그인 실패",Toast.LENGTH_LONG).show();

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException { // 통신 성공
            String result = response.body().string();
            Log.d("server response:",result);
        }
    };
}
