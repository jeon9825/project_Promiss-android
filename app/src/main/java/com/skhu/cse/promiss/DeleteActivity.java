package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.skhu.cse.promiss.Items.UserData;
import com.skhu.cse.promiss.custom.PromissDialog;
import com.skhu.cse.promiss.database.BasicDB;
import com.skhu.cse.promiss.server.GetJson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DeleteActivity extends AppCompatActivity {

    RelativeLayout after_delete;
    RelativeLayout delete_default;

    EditText password;
    PromissDialog builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        after_delete= findViewById(R.id.delete_success);
        delete_default=findViewById(R.id.delete_default_body);

        password=findViewById(R.id.delete_editText_check);

        findViewById(R.id.delete_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.delete_default_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               builder=new PromissDialog.Builder(DeleteActivity.this)
                        .setTitle("회원 탈퇴")
                        .setMessage("정말로 탈퇴하시겠습니까?")
                        .addCancelListener("취소", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                builder.dismiss();
                            }
                        }).addOkayListener("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                new Thread()
                                {
                                    @Override
                                    public void run() {
                                        GetJson json = GetJson.getInstance();

                                        json.requestPost("api/User/delete",callback,"index", UserData.shared.getId()+"");
                                    }
                                }.run();

                                builder.dismiss();
                            }
                        }).build();
               builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //배경 뒤에 안보이기
               builder.show();

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

                    if(object.getString("result").equals("OK"))
                    {
                        BasicDB.setId(getApplicationContext(),-1);
                        BasicDB.setAppoint(getApplicationContext(),-1);
                        BasicDB.setPREF_Result(getApplicationContext(),-1);

                        DeleteActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UserDeleteSuccess();
                            }
                        });
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

    public void UserDeleteSuccess(){
        delete_default.setVisibility(View.GONE);
        after_delete.setVisibility(View.VISIBLE);

        //로그인 버튼 클릭시 로그인 화면만 남겨두고 나머지 엑티비티 종료

        Intent intent= new Intent(DeleteActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
