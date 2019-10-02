package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skhu.cse.promiss.Items.UserData;
import com.skhu.cse.promiss.Items.UserItem;
import com.skhu.cse.promiss.custom.PromissDialog;
import com.skhu.cse.promiss.database.BasicDB;
import com.skhu.cse.promiss.server.GetJson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    TextView textViewName;
    TextView textViewAddress1;
    TextView textViewAddress2;
    TextView textViewDate;
    TextView textViewTime;
    TextView textViewFine;
    TextView textViewCount; //사람명수
    ImageButton imageButtonAdd;
    PromissDialog builder;
    RecyclerView recyclerView;
    UserListAdapter adapter;

    ArrayList<UserItem> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        textViewName = findViewById(R.id.detail_name);
        textViewAddress1 = findViewById(R.id.detail_location_address1);
        textViewAddress2 = findViewById(R.id.detail_location_address2);
        textViewDate = findViewById(R.id.detail_date);
        textViewTime = findViewById(R.id.detail_time);
        textViewFine = findViewById(R.id.detail_fine);
        textViewCount = findViewById(R.id.detail_count);

        imageButtonAdd=findViewById(R.id.detail_add_friend);
        recyclerView = findViewById(R.id.detail_friend);

        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, AddFriendActivity.class);
                intent.putExtra("data",arrayList);
                startActivity(intent);
            }
        });

        findViewById(R.id.detail_button_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder = new PromissDialog.Builder(DetailActivity.this)
                        .setTitle("약속 나가기")
                        .setMessage("현재 약속을 다시 참여하고 싶으면,\n현재 약속 멤버가 다시 초대를 해주어야 합니다\n정말로 나가시겠습니까?")
                        .addCancelListener("취소", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                builder.dismiss();

                            }
                        }).addOkayListener("나가기", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                builder.dismiss();
                                new Thread() {
                                    @Override
                                    public void run() {
                                        int appoint_id = BasicDB.getAppoint(getApplicationContext());
                                        int id = UserData.shared.getId();
                                        GetJson json = GetJson.getInstance();

                                        json.requestPost("api/Appointment/leave", leave, "appoint_id", "" + appoint_id,"id",""+id);
                                    }
                                }.run();
                            }
                        }).build();
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //배경 뒤에 안보이기
                builder.show();
            }
        });

        findViewById(R.id.detail_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        adapter = new UserListAdapter(this, arrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        new Thread() {
            @Override
            public void run() {
                int id = BasicDB.getAppoint(getApplicationContext());

                GetJson json = GetJson.getInstance();

                json.requestPost("api/Appointment/getAppointment_detail", callback, "id", "" + id);
            }
        }.run();
    }

    private Callback leave =new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            DetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DetailActivity.this, "네트워크를 확인해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String result = response.body().string();
            Log.d("server", result);

            try {
                JSONObject object = new JSONObject(result);

                if (object.getString("result").equals("OK")) {

                    BasicDB.setAppoint(getApplicationContext(),-1);
                    finish();
                } else {
                    DetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DetailActivity.this, "현재 서버가 문제가 있습니다.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            }catch (JSONException e){
                DetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "현재 서버가 문제가 있습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        }
    };


    private Callback callback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            DetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DetailActivity.this, "네트워크를 확인해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String result = response.body().string();
            Log.d("server", result);

            try {
                JSONObject object = new JSONObject(result);
                object = object.getJSONObject("data");
                String name = object.getString("name");
                String address1 = object.getString("address");
                String address2 = object.getString("detail");
                String date = object.getString("date");
                String time = object.getString("date_time");
                String[] arr = time.split(":");
                Integer intHour = Integer.parseInt(arr[0]);
                String ampm = intHour >= 12 ? "오후 " : "오전 ";
                arr[0] = intHour >= 12 ? intHour - 12 + "" : intHour + "";
                String timeText = ampm + arr[0] + ":" + arr[1];
                String fine = object.getInt("Fine_time") + "분마다 " + object.getInt("Fine_money") + "원";
                JSONArray members = object.getJSONArray("members");

                for (int i = 0; i < members.length(); i++) {

                    JSONObject item = members.getJSONObject(i);
                    arrayList.add(new UserItem(item.getInt("id"), item.getString("user_name"), true));

                }

                DetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewName.setText(name);
                        textViewAddress1.setText(address1);
                        textViewAddress2.setText(address2);
                        textViewDate.setText(date);
                        textViewTime.setText(timeText);
                        textViewFine.setText(fine);
                        textViewCount.setText(members.length()+"명");
                        adapter.notifyDataSetChanged();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


}
