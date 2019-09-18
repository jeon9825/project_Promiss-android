package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText editText_ID;
    EditText editText_PW;
    EditText editText_PW_check;
    InputMethodManager inputManager;
    RelativeLayout loginView;
    View center_view;
    SoftKeyboard softKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        center_view = findViewById(R.id.login_center_view);
//        inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        loginView=findViewById(R.id.login);
//        softKeyboard =new SoftKeyboard(loginView,inputManager);
//        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
//            @Override
//            public void onSoftKeyboardHide() {
//
//            }
//            @Override
//            public void onSoftKeyboardShow() {
//                RegisterActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        center_view.setVisibility(View.GONE);
//                    }
//                });
//            }
//        });
        editText_ID = findViewById(R.id.register_edit_id);
        editText_PW = findViewById(R.id.register_edit_password);
        editText_PW_check = findViewById(R.id.register_edit_password_check);
        Button button_OK = findViewById(R.id.register_button_ok);
        button_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = editText_ID.getText().toString();
                final String pw = editText_PW.getText().toString();
                final String pw_check = editText_PW_check.getText().toString();
                if (id != null && id.equals("")) {
                    Toast.makeText(getApplicationContext(),"아이디를 입력하십시오.",Toast.LENGTH_LONG).show();
                }
                if(pw!=null&&pw.equals("")){
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하십시오.",Toast.LENGTH_LONG).show();
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        GetJson getJson = GetJson.getInstance();
                        getJson.requestPost("api/User/register", callback, "id", id, "pw", pw);
                    }
                }.run();
            }
        });
    }

    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) { // 통신 실패

            RegisterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RegisterActivity.this, "네트워크 연결 실패", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException { // 통신 성공
            String result = response.body().string();
            Log.d("server response:", result);
            try {
                JSONObject object = new JSONObject(result);

                if (object.getString("result").equals("OK")) { //성공
//                    =object.getString("data")
                    Intent intent = new Intent(RegisterActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                } else { //실패
                    final String message = object.getString("message");
                    RegisterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // JSONArray array;
        }
    };
}