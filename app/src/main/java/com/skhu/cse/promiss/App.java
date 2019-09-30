package com.skhu.cse.promiss;


import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;



public class App extends Application {

    public static final String CHAANEL_ID="PromissServiceChannel";

    private static volatile App instance = null;



    public static App getGlobalApplicationContext() {
        if(instance == null) {
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        instance = this;

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

    private void createNotificationChannel(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel serviceChannel=new NotificationChannel(
                    CHAANEL_ID,
                    "Promiss Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

    }
}
