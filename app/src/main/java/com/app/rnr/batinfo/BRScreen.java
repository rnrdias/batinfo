package com.app.rnr.batinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by RNR on 14/01/2018.
 */

public class BRScreen extends BroadcastReceiver {
    final String TAG = this.getClass().getName();
    private Runnable screenOn;
    private Runnable screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.d(TAG,"Screen ON");
            screenOn.run();
        } else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d(TAG,"Screen OFF");
            screenOff.run();
        }
    }


    public void setRunnableScreenOn(Runnable r){
        screenOn = r;
    }

    public void setRunnableScreenOff(Runnable r){
        screenOff = r;
    }
}
