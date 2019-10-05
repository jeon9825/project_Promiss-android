package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skhu.cse.promiss.Items.UserData;
import com.skhu.cse.promiss.database.BasicDB;
import com.skhu.cse.promiss.server.GetJson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChangePassword extends AppCompatActivity {


    EditText editText_pw;
    EditText editText_pw_check;
    TextView message;
    boolean check =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editText_pw=findViewById(R.id.change_password_edit_pw);
        editText_pw_check  = findViewById(R.id.change_password_edit_pw_ok);
        message = findViewById(R.id.change_password_check_pw);



        editText_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editText_pw.getText().toString().equals(editable.toString()))
                {
                    check=true;
                    message.setText("일치");
                    message.setTextColor(getResources().getColor(R.color.same));
                }else
                {
                    check=false;
                    message.setText("비밀번호가 일치하지않습니다");
                    message.setTextColor(getResources().getColor(R.color.error));
                }
            }
        });


        editText_pw_check.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editText_pw.getText().toString().equals(editable.toString()))
                {
                    check=true;
                    message.setText("일치");
                    message.setTextColor(getResources().getColor(R.color.same));
                }else
                {
                    check=false;
                    message.setText("비밀번호가 일치하지않습니다");
                    message.setTextColor(getResources().getColor(R.color.error));
                }
            }
        });


        findViewById(R.id.change_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText_pw.getText().toString().equals(""))
                {
                    Toast.makeText(ChangePassword.this,"비밀번호를 입력해주세요",Toast.LENGTH_LONG).show();
                }else
                {
                    new Thread()
                    {
                        @Override
                        public void run() {
                            GetJson json =GetJson.getInstance();

                            json.requestPost("api/User/changePassword",callback,"index", UserData.shared.getId()+"","password",editText_pw.getText().toString());
                        }
                    }.run();
                }
            }
        });


        findViewById(R.id.change_password_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private Callback callback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String s = response.body().string();

            try{

                JSONObject object = new JSONObject(s);

                if(object.getInt("result")==1000)
                {
                    String message = object.getString("data");
                }else
                {
                    finish();
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    };
}
