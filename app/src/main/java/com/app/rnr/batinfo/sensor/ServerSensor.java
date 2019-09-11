package com.app.rnr.batinfo.sensor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.app.rnr.batinfo.BRScreen;
import com.app.rnr.batinfo.MainActivity;
import com.app.rnr.batinfo.NotificationActivity;
import com.app.rnr.batinfo.R;


/**
 * Created by RNR on 06/01/2018.
 */

public class ServerSensor extends Service {
    final String TAG = this.getClass().getName();
    public final SensorManager sensors = SensorManager.instance;
    private RemoteViews contentView;
    private BRScreen standbay;
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    public final Handler refresh = new Handler();

    public class LocalBinder extends Binder {
        public ServerSensor getService() {
            // Return this instance of LocalService so clients can call public methods
            return ServerSensor.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Bind");
        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Start");

        sensors.init(this);
        refresh.post(refreshRun);
        notificationInit(this);

        if (intent == null || intent.getBooleanExtra("notification", true)) {
            notificationAdd();
            Log.d(TAG, "add");
        } else {
            notificationRm();
            Log.d(TAG, "rm");
        }

        standbay = new BRScreen();
        standbay.setRunnableScreenOn(screenOn);
        standbay.setRunnableScreenOff(screenOff);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        this.registerReceiver(standbay, filter);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Stop");
        super.onDestroy();
        refresh.removeCallbacks(refreshRun);
        this.unregisterReceiver(standbay);
    }

    //_________________________________Server Application_______________________________

    private Runnable refreshRun = new Runnable() {
        @Override
        public void run() {
            //boolean isSceenAwake = ((PowerManager) getSystemService(Context.POWER_SERVICE)).isScreenOn();
            //if (isSceenAwake) {
            Log.d(TAG, "Refresh");
            sensors.refresh();
            //}
            refresh.postDelayed(refreshRun, 1000);
        }
    };

    //__________________________________Notification____________________________________
    boolean notificationEnable = false;
    NotificationActivity notificationActivity;
    private final Context context = this;

    private void notificationInit(Context context) {
        contentView = new RemoteViews(this.getPackageName(), R.layout.notification);
        contentView.setImageViewResource(R.id.imageView, R.drawable.bat_b);
        contentView.setTextViewText(R.id.textView, "BV: " + sensors.getSenBatVol() + " BT:" + sensors.getSenBatTemp() + "\nBA:" + sensors.getSenBatAmp());
        notificationActivity = new NotificationActivity(context, MainActivity.class, contentView);
        notificationActivity.setSmallIcon(R.drawable.bat_notif_b);
    }

    public void notificationAdd() {
        if (notificationActivity != null && !notificationEnable) {
            refresh.post(notificationRefresh);
            notificationEnable = true;
        }

    }


    public void notificationRm() {
        if (notificationActivity != null && notificationEnable) {
            refresh.removeCallbacks(notificationRefresh);
            notificationActivity.notificationHide();
            notificationEnable = false;
        }

    }

    public boolean isNotificationEnable() {
        return notificationEnable;
    }


    Runnable notificationRefresh = new Runnable() {
        public void run() {
            contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
            notificationEnable = true;
            if (sensors.getBatAmpMed() == sensors.BAT_AMP_MED_BAIX) {
                contentView.setImageViewResource(R.id.imageView, R.drawable.bat_b);
                notificationActivity.setSmallIcon(R.drawable.bat_notif_b);
            } else if (sensors.getBatAmpMed() == sensors.BAT_AMP_MED_MED) {
                contentView.setImageViewResource(R.id.imageView, R.drawable.bat_m);
                notificationActivity.setSmallIcon(R.drawable.bat_notif_m);
            } else if (sensors.getBatAmpMed() == sensors.BAT_AMP_MED_ALT) {
                contentView.setImageViewResource(R.id.imageView, R.drawable.bat_a);
                notificationActivity.setSmallIcon(R.drawable.bat_notif_a);
            } else {
                contentView.setImageViewResource(R.id.imageView, R.drawable.bat_nd);
                notificationActivity.setSmallIcon(R.drawable.bat_notif_nd);
            }

            contentView.setTextViewText(R.id.textView, "BV: " + sensors.getSenBatVol() + " BT:" + sensors.getSenBatTemp() + "\nBA:" + sensors.getSenBatAmp());
            notificationActivity.notificationVisible(contentView);

            refresh.postDelayed(notificationRefresh, 500);
            Log.d(TAG, "notification Refresh");
        }
    };


//_____________________________________POWER________________________________________________

    Runnable screenOn = new Runnable() {
        @Override
        public void run() {
            refresh.post(refreshRun);
            if (notificationActivity != null && notificationEnable) {
                refresh.post(notificationRefresh);
            }

        }
    };

    Runnable screenOff = new Runnable() {
        @Override
        public void run() {
            refresh.removeCallbacks(refreshRun);
            if (notificationActivity != null && notificationEnable) {
                refresh.removeCallbacks(notificationRefresh);
            }
        }
    };
}
