package com.skhu.cse.promiss.Service;



import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.skhu.cse.promiss.Items.UserData;
import com.skhu.cse.promiss.MapActivity;
import com.skhu.cse.promiss.R;
import com.skhu.cse.promiss.server.GetJson;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static com.skhu.cse.promiss.App.CHAANEL_ID;


public class PromissService extends Service implements LocationListener {

    private final String id = "" + UserData.shared.getId();
    private final String name = UserData.shared.getName();
    Location location;

    protected LocationManager locationManager;

    //push 알람

    NotificationManagerCompat manager;
    NotificationCompat.Builder Init;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    //서비스가 죽었다가 다시 실행이 될 때, 호출되는 함수
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.d("test","실행");
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return START_NOT_STICKY;
        } else {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    60000,
                    1,
                    this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    60000,
                    1,
                    this);
        }

        //알람 등록
        {
            manager = NotificationManagerCompat.from(this);
            Intent FineIntent = new Intent(this, MapActivity.class);

            PendingIntent FinePendingIntent = PendingIntent.getActivity(this
                    , 0, FineIntent, 0); //알람을 눌렀을 때 해당 엑티비티로


            Init = new NotificationCompat.Builder(this, CHAANEL_ID)
                    .setContentTitle("Pro-Miss")
                    .setAutoCancel(true)// 사용자가 알람을 탭했을 때, 알람이 사라짐
                    .setContentText("위치정보를 수신중입니다")
                    .setSmallIcon(R.drawable.flag_icon)
                    .setContentIntent(FinePendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


            startForeground(10, Init.build());

            //  manager.notify(30, New_Alert);
            return START_STICKY;  //서비스가 종료되어도 다시 시작
        }
    }


        @Override
        public void onDestroy () {
            super.onDestroy();
            try {
                locationManager.removeUpdates(this);//더 이상 필요하지 않을 때, 자원 누락을 방지하기 위해

            } catch (NullPointerException e) {
                //locationmanager가 null일 때
            }
        }


        @Nullable
        @Override
        public IBinder onBind (Intent intent){
            return null;
        }




        //사용자의 gps가 변할 때, 서버와 통신하는 Callback 함수
        @Override
        public void onLocationChanged ( final Location location){
            Log.d("gps", "LocationChanged");
            this.location = location;
//

            new Thread() {
                @Override
                public void run() {
                    GetJson connection = GetJson.getInstance();

                    connection.requestPost("api/User/gpsUpdate", callback, "id",id,
                            "latitude",location.getLatitude()+"","longitude",location.getLongitude()+"");
                }
            }.run();

        }


        //서버통신 후 JSON 파싱
        private Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();

                JSONObject object = null;
                try {
                    object = new JSONObject(s);
                    if (object.getString("result").equals("OK")) {

                    } else {
//                    sendErrorMessage(object.getString("message"));
                        onDestroy();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        // LocationListener
        ////////////////////////////////////////////////////////////////////////////////////////
        @Override
        public void onStatusChanged (String provider,int status, Bundle extras){
            Log.d("gps", "statusChange");
        }

        @Override
        public void onProviderEnabled (String provider){
            Log.d("gps", "providerEnabled");
        }

        @Override
        public void onProviderDisabled (String provider){
            Log.d("gps", "providerDisabled");
        }


}
