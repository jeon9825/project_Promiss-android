package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skhu.cse.promiss.Items.UserItem;
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

        findViewById(R.id.detail_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
