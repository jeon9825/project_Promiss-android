package com.skhu.cse.promiss;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;

import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dnkilic.waveform.WaveView;



import java.util.ArrayList;

public class SiriActivity extends Activity {

    private WaveView mWaveView;
    PowerManager powerManager;

    final int RequsetCheck=1;
    final int SET_TEXT_REQUEST=2;
    boolean hasRequest=true;
    PowerManager.WakeLock wakeLock;

    LinearLayout linearLayout;
    ArrayList<TextView> arrayList=new ArrayList<>();
    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siri);
        mWaveView = findViewById(R.id.vw2);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        linearLayout=findViewById(R.id.siri_textlist);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);


        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKELOCK");

        wakeLock.acquire(); // WakeLock 깨우기

        wakeLock.release(); // WakeLock 해제

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                start(new View(getApplicationContext()));
                startSpeech(new View(getApplicationContext()));
            }
        }, 1000);// 0.5초 정도 딜레이를 준 후 시작


        mHdrVoiceRecoState.sendEmptyMessage(RequsetCheck);
    }
    private Handler mHdrVoiceRecoState = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {

                case RequsetCheck:
                {
                    if(hasRequest==false)
                        finish();
                    else
                        hasRequest=false;
                    sendEmptyMessageDelayed(RequsetCheck, 15000);
                    break;
                }


                default:
                    super.handleMessage(msg);
            }

        }
    };

    public void reset() {

        mWaveView.stop();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);// 5초 정도 딜레이를 준 후 시작
    }

    public void start(View v) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWaveView.initialize(dm);
    }

    public void startSpeech(View v) {
        mWaveView.speechStarted();
    }

    public void endSpeech() {
        mWaveView.speechEnded();
    }

    public void pauseSpeech() {
        mWaveView.speechPaused();
    }


    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver ,new IntentFilter("Promiss-event-name")
        );

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }



    public void AddTextView(String send, String message){
        TextView view=new TextView(SiriActivity.this);
        view.setText(message);
        view.setTextSize(18.5f);
        if(arrayList.size()>4)
        {
            TextView tv=arrayList.get(0);
            arrayList.remove(tv);
            linearLayout.removeView(tv);
        }


        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        linearLayout.addView(view,layoutParams);


        arrayList.add(view);
        if(send.equals("my"))
        {
            view.setGravity(Gravity.RIGHT);
        }else
        {
            view.setGravity(Gravity.LEFT);
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    { @Override public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub // Get extra data included in the

        String bysend= intent.getStringExtra("send");
        if(bysend.equals("start"))
            startSpeech(null);
        else if(bysend.equals("reset")){
            reset();
        }else if(bysend.equals("pause"))
        {
            pauseSpeech();
        }
        else { //send is equal (my or chatbot)
            hasRequest=true;
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);

            AddTextView(bysend,message);
        }

    }
    };


    @Override
    public void onBackPressed() {  // 백버튼 막기위해

    }
}