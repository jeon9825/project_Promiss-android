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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.skhu.cse.promiss.keyboard.SoftKeyboard;
import com.skhu.cse.promiss.server.GetJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText editText_id;
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
        loginView = findViewById(R.id.login);
        softKeyboard = new SoftKeyboard(loginView, inputManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        center_view.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        center_view.setVisibility(View.GONE);
                    }
                });
            }
        });
        editText_id = (EditText) findViewById(R.id.login_edit_id);
        editText_password = (EditText) findViewById(R.id.login_edit_password);
        Button b = (Button) findViewById(R.id.login_button);
        Button register_button = findViewById(R.id.register_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = editText_id.getText().toString();
                final String password = editText_password.getText().toString();
                if (id != null && password != null && id.equals("") && password.equals("")) {
                    Toast.makeText(getApplicationContext(), "입력하지않았습니다.", Toast.LENGTH_LONG).show();
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        GetJson getJson = GetJson.getInstance();
                        getJson.requestPost("api/User/Login", callback, "id", id, "pw", password);
                    }
                }.run();
//                Intent intent = new Intent(LoginActivity.this, MapActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        }
        );
    }

    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) { // 통신 실패

            LoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "네트워크 연결 실패", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException { // 통신 성공
            String result = response.body().string();
            Log.d("server response:", result);
            try {
                JSONObject object = new JSONObject(result);

                if (object.getInt("result") == 2000) { //성공
//                    =object.getString("data")
                    Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                } else { //실패
                    final String message = object.getString("message");
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
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
