package com.lagel.com.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class ProductReceiver extends BroadcastReceiver {

    public static final String TAG = "ProductReceiver";
    public static final String REPEAT_INTERVAL_KEY = "repeat_interval";
    public static final String TRIGGER_TIME_KEY = "trigger_time";


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Start Service Receiver Called ");
        Intent bkgService = new Intent(context, ProductService.class);
        context.startService(bkgService);
    }
}
