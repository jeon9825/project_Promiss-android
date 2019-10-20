package com.skhu.cse.promiss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.skhu.cse.promiss.Items.UserData;
import com.skhu.cse.promiss.database.BasicDB;
import com.skhu.cse.promiss.server.GetJson;

public class LoadingActivity extends AppCompatActivity {

    final private int MY_PERMISSIONS_REQUEST_READ_CONTACTS =200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        if (ContextCompat.checkSelfPermission(LoadingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(LoadingActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted

            ActivityCompat.requestPermissions(LoadingActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }else {
            GoMain();
        }
    }

    public void GoMain(){
        if (BasicDB.getID(getApplicationContext()) == -1) //초기화 or 다시 로그인
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoadingActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1400);
        } else //이미 로그인
        {
            UserData data = UserData.shared;
            data.setId(BasicDB.getID(getApplicationContext()));
            data.setName(BasicDB.getUserId(getApplicationContext()));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoadingActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1400);

        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    GoMain();
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(LoadingActivity.this,"권한을 허용해주세요",Toast.LENGTH_LONG).show();
                    finish();
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
