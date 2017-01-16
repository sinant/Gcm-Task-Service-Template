package com.sinantalebi.gcmnetworkmanager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sinan Talebi on 7.1.2017.
 */

public class TaskService extends GcmTaskService {
    private static final String TAG = "TaskService";


    public static final String GCM_ONEOFF_TAG = "oneoff|[0,0]";
    public static final String GCM_REPEAT_TAG = "repeat|[7200,1800]";

    @Override
    public void onInitializeTasks() {
        //called when app is updated to a new version, reinstalled etc.
        //you have to schedule your repeating tasks again
        super.onInitializeTasks();
    }

    @Override
    public int onRunTask(final TaskParams taskParams) {
        final Bundle extras = taskParams.getExtras();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (taskParams.getTag().equals(GCM_ONEOFF_TAG)) {
                    Log.d(TAG, "ONEOFF executed " + extras.get("foo"));
                } else if (taskParams.getTag().equals(GCM_REPEAT_TAG)) {
                    Log.d(TAG, "REPEATING executed " + extras.get("foo"));
                }
            }
        });
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    public static void scheduleOneOff(Context context) {
        Bundle data = new Bundle();
        data.putString("foo", "bar oneoff");
        try {
            OneoffTask oneoff = new OneoffTask.Builder()
                    //specify target service - must extend GcmTaskService
                    .setService(TaskService.class)
                    //tag that is unique to this task (can be used to cancel task)
                    .setTag(GCM_ONEOFF_TAG)
                    //executed between 0 - 10s from now
                    .setExecutionWindow(0, 10)
                    //set required network state, this line is optional
                    .setRequiredNetwork(Task.NETWORK_STATE_ANY)
                    //request that charging must be connected, this line is optional
                    .setRequiresCharging(false)
                    //set some data we want to pass to our task
                    .setExtras(data)
                    //if another task with same tag is already scheduled, replace it with this task
                    .setUpdateCurrent(true)
                    .build();
            GcmNetworkManager.getInstance(context).schedule(oneoff);
            Log.v(TAG, "oneoff task scheduled");
        } catch (Exception e) {
            Log.e(TAG, "One off task scheduling failed", e);
        }
    }

    public static void scheduleRepeat(Context context) {
        //in this method, single Repeating task is scheduled (the target service that will be called is TaskService.class)
        Bundle data = new Bundle();
        data.putString("foo", "bar repeat");
        try {
            PeriodicTask periodic = new PeriodicTask.Builder()
                    //specify target service - must extend GcmTaskService
                    .setService(TaskService.class)
                    //repeat every 60 seconds
                    .setPeriod(60)
                    //specify how much earlier the task can be executed (in seconds)
                    .setFlex(30)
                    //tag that is unique to this task (can be used to cancel task)
                    .setTag(GCM_REPEAT_TAG)
                    //whether the task persists after device reboot
                    .setPersisted(true)
                    //if another task with same tag is already scheduled, replace it with this task
                    .setUpdateCurrent(true)
                    //set required network state, this line is optional
                    .setRequiredNetwork(Task.NETWORK_STATE_ANY)
                    //request that charging must be connected, this line is optional
                    .setRequiresCharging(false)
                    .setExtras(data)
                    .build();
            GcmNetworkManager.getInstance(context).schedule(periodic);
            Log.v(TAG, "repeating task scheduled");
        } catch (Exception e) {
            Log.e(TAG, "Repeating task scheduling failed", e);
        }
    }

    public static void cancelOneOff(Context context) {
        GcmNetworkManager
                .getInstance(context)
                .cancelTask(GCM_ONEOFF_TAG, TaskService.class);
    }

    public static void cancelRepeat(Context context) {
        GcmNetworkManager
                .getInstance(context)
                .cancelTask(GCM_REPEAT_TAG, TaskService.class);
    }

    public static void cancelAll(Context context) {
        GcmNetworkManager
                .getInstance(context)
                .cancelAllTasks(TaskService.class);
    }
}
