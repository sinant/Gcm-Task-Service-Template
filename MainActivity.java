package com.sinantalebi.gcmnetworkmanager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null)
            startTaskService();
        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    Intent i = new Intent(this, TaskService.class);
                    startService(i); // OK, init GCM
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initViews() {
        Button oneoff = (Button) findViewById(R.id.schedule_oneoff);
        oneoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskService.scheduleOneOff(v.getContext());
            }
        });
        Button repeat = (Button) findViewById(R.id.schedule_repeating);
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskService.scheduleRepeat(v.getContext());
            }
        });
        Button cancelOneoff = (Button) findViewById(R.id.cancel_oneoff);
        cancelOneoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskService.cancelOneOff(v.getContext());
            }
        });
        Button cancelRepeat = (Button) findViewById(R.id.cancel_repeat);
        cancelRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskService.cancelRepeat(v.getContext());
            }
        });
        Button cancelAll = (Button) findViewById(R.id.cancel_all);
        cancelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskService.cancelAll(v.getContext());
            }
        });
    }

    private void startTaskService() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            onActivityResult(REQUEST_GOOGLE_PLAY_SERVICES, Activity.RESULT_OK, null);
        } else if (api.isUserResolvableError(isAvailable) &&
                api.showErrorDialogFragment(this, isAvailable, REQUEST_GOOGLE_PLAY_SERVICES)) {
            // wait for onActivityResult call
        } else {
            Toast.makeText(this, api.getErrorString(isAvailable), Toast.LENGTH_LONG).show();
        }
    }
}