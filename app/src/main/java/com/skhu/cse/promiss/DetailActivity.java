package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.skhu.cse.promiss.database.BasicDB;
import com.skhu.cse.promiss.server.GetJson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
                String address1 = object.getString("address");
                String date = object.getString("date");
                String time = object.getString("date_time");
                String[] arr = time.split(":");
                Integer intHour = Integer.parseInt(arr[0]);
                String ampm = intHour >= 12 ? "오후 " : "오전 ";
                arr[0] = intHour >= 12 ? intHour - 12 + "" : intHour + "";
                String timeText = ampm+arr[0]+":"+arr[1];
                String fine = object.getInt("Fine_time") + "분마다 " + object.getInt("Fine_money") + "원";
                DetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewAddress1.setText(address1);
                        textViewDate.setText(date);
                        textViewTime.setText(timeText);
                        textViewFine.setText(fine);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


}
