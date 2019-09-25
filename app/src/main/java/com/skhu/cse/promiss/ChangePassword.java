package com.skhu.cse.promiss;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skhu.cse.promiss.Items.UserData;
import com.skhu.cse.promiss.database.BasicDB;
import com.skhu.cse.promiss.server.GetJson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.Watchable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.skhu.cse.promiss.Items.UserData.shared;

public class ChangePassword extends AppCompatActivity {

    EditText editText_PW;
    EditText editText_PW_check;
    Button button; // 비번 변경 버튼
    TextView textView_PW_check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editText_PW = findViewById(R.id.change_password_edit_pw);
        editText_PW_check = findViewById(R.id.change_password_edit_pw_ok);
        textView_PW_check =findViewById(R.id.change_password_check_pw);
        editText_PW_check.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals(editText_PW.getText().toString())) {
                    textView_PW_check.setText("일치 ");
                    textView_PW_check.setTextColor(getResources().getColor(R.color.same));
                }
            }
        });
        button=findViewById(R.id.change_password_button);
        button.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                final String pw = editText_PW.getText().toString();
                final String pwOk = editText_PW_check.getText().toString();

                if(pw==null || pw.equals("")){
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하십시오.",Toast.LENGTH_LONG).show();
                }

                final int id = shared.getId();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        GetJson getJson = GetJson.getInstance();
                        getJson.requestPost("api/User/changePassword",callback,"index", id+"","password",pw);
                    }
                }.run();
            }
        });

    }
    private Callback callback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

            ChangePassword.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChangePassword.this, "네트워크 연결 실패", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String result = response.body().string();
            Log.d("server response:", result);
            try{
                JSONObject object = new JSONObject(result);
                if(object.getInt("result")==2000){
                    object = object.getJSONObject("data");
                    BasicDB.setUserInfo(getApplicationContext(),object.getString("user_name"),object.getString("user_pw"),object.getInt("id"));
                    Intent intent = new Intent(ChangePassword.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    final String message = object.getString("message");
                    ChangePassword.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePassword.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

}
