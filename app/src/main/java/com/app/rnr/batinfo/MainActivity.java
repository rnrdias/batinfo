package com.app.rnr.batinfo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rnr.batinfo.sensor.ServerSensor;

public class MainActivity extends AppCompatActivity {
    final String TAG = this.getClass().getName();

    final String mensPositivo = "Ligado";
    final String mensNegativo = "Desligado";
    final String mensIndisponivel = "Indisponivel";
    final String mensAlto = "Alto";
    final String mensMedio = "Medio";
    final String mensBaixo = "Baixo";

    TextView textBatAmp;
    TextView textConsumo;
    TextView textEstim;
    ImageView imageBat;

    TextView textVolt;
    TextView textAmp;
    TextView textPorcent;
    TextView textTemp;
    TextView textSaud;

    TextView textCarrVolt;
    TextView textCarrAmp;
    TextView textCarrUsb;
    TextView textCarrFont;
    TextView textCarrWire;


    LinearLayout linLayBat;
    LinearLayout linLayBatVolt;
    LinearLayout linLayBatAmp;
    LinearLayout linLayBatPorcent;
    LinearLayout linLayBatTemp;
    LinearLayout linLayBatSaud;
    LinearLayout linLayCarr;
    LinearLayout linLayCarrVolt;
    LinearLayout linLayCarrAmp;
    LinearLayout linLayCarrUSB;
    LinearLayout linLayCarrFont;
    LinearLayout linLayCarrWireless;


    Menu menu;
    MenuItem menuNotification;
    ServerSensor mService;
    boolean mBound = false;
    private boolean notificationEnable;

    private final float consumoMedio = 500f;
    private final float consumoBaixo = 250f;
    //BroadcastReceiver a;

    //_____________________________Active_____________________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textBatAmp = (TextView) findViewById(R.id.textBatAmp);
        textConsumo = (TextView) findViewById(R.id.textConsumo);
        textEstim = (TextView) findViewById(R.id.textEstim);
        imageBat = (ImageView) findViewById(R.id.imageBat);

        textVolt = (TextView) findViewById(R.id.textVolt);
        textAmp = (TextView) findViewById(R.id.textAmp);
        textPorcent = (TextView) findViewById(R.id.textPorcent);
        textTemp = (TextView) findViewById(R.id.textTemp);
        textSaud = (TextView) findViewById(R.id.textSaud);

        textCarrVolt = (TextView) findViewById(R.id.textCarrVolt);
        textCarrAmp = (TextView) findViewById(R.id.textCarrAmp);
        textCarrUsb = (TextView) findViewById(R.id.textCarrUsb);
        textCarrFont = (TextView) findViewById(R.id.textCarrFont);
        textCarrWire = (TextView) findViewById(R.id.textCarrWire);

        linLayBat = (LinearLayout) findViewById(R.id.LinLayBat);
        linLayBatVolt = (LinearLayout) findViewById(R.id.LinLayBatVolt);
        linLayBatAmp = (LinearLayout) findViewById(R.id.LinLayBatAmp);
        linLayBatPorcent = (LinearLayout) findViewById(R.id.LinLayBatPorcent);
        linLayBatTemp = (LinearLayout) findViewById(R.id.LinLayBatTemp);
        linLayBatSaud = (LinearLayout) findViewById(R.id.LinLayBatSaud);

        linLayCarr = (LinearLayout) findViewById(R.id.LinLayCarr);
        linLayCarrVolt = (LinearLayout) findViewById(R.id.LinLayCarrVolt);
        linLayCarrAmp = (LinearLayout) findViewById(R.id.LinLayCarrAmp);
        linLayCarrUSB = (LinearLayout) findViewById(R.id.LinLayCarrUSB);
        linLayCarrFont = (LinearLayout) findViewById(R.id.LinLayCarrFont);
        linLayCarrWireless = (LinearLayout) findViewById(R.id.LinLayCarrWireless);

        connectService();
/*
        a = new BRScreen();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        this.registerReceiver(a, filter);
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mService != null)
            mService.refresh.post(refresh);
    }

    protected void onPause() {
        super.onPause();
        if (mService != null)
            mService.refresh.removeCallbacks(refresh);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
        //this.unregisterReceiver(a);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        menu.findItem(R.id.item_notif).setChecked(notificationEnable);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_notif:
                notificationEnable = !notificationEnable;
                if (notificationEnable)
                    mService.notificationAdd();
                else
                    mService.notificationRm();
                item.setChecked(notificationEnable);
                Log.d("TAG", "Click");
                return true;
            case R.id.item_config:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Context context = this;
    //________________________________Refresh________________________________________________________
    Runnable refresh = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "refresh");

            textBatAmp.setText(mService.sensors.getSenBatAmp());
            textEstim.setText(mService.sensors.getSenBatAmp());

            if (mService.sensors.getBatAmpMed() == mService.sensors.BAT_AMP_MED_BAIX) {
                textConsumo.setText("Baixo");
                imageBat.setImageResource(R.drawable.bat_b);
            } else if (mService.sensors.getBatAmpMed() == mService.sensors.BAT_AMP_MED_MED) {
                textConsumo.setText("Medio");
                imageBat.setImageResource(R.drawable.bat_m);
            } else if (mService.sensors.getBatAmpMed() == mService.sensors.BAT_AMP_MED_ALT) {
                textConsumo.setText("Alto");
                imageBat.setImageResource(R.drawable.bat_a);
            } else {
                textConsumo.setText("Undefined");
                imageBat.setImageResource(R.drawable.bat_nd);
            }

            textVolt.setText(mService.sensors.getSenBatVol());
            textAmp.setText(mService.sensors.getSenBatAmp());
            textPorcent.setText(mService.sensors.getSenBatPorcent());
            textTemp.setText(mService.sensors.getSenBatTemp());
            textSaud.setText(mService.sensors.getSenBatSaud());

            textCarrVolt.setText(mService.sensors.getSenCarrVolt());
            textCarrUsb.setText(mService.sensors.getSenCarrUsb());
            textCarrFont.setText(mService.sensors.getSenCarrFont());
            textCarrWire.setText(mService.sensors.getSenCarrWireless());


            textEstim.setText(mService.sensors.getTempEstim());


            mService.refresh.postDelayed(refresh, 500);

        }
    };
    //__________________________________Notificação_________________________________________
    Runnable viewVisibleRefresh = new Runnable() {
        @Override
        public void run() {

            boolean isBattFullInvisible = true;
            if (mService.sensors.getModeBatVolt() == mService.sensors.MODE_DISABLE) {
                linLayBatVolt.setVisibility(View.GONE);
            } else {
                linLayBatVolt.setVisibility(View.VISIBLE);
                isBattFullInvisible = false;
            }

            if (mService.sensors.getModeBatAmp() == mService.sensors.MODE_DISABLE) {
                linLayBatAmp.setVisibility(View.GONE);
            } else {
                linLayBatAmp.setVisibility(View.VISIBLE);
                isBattFullInvisible = false;
            }

            if (mService.sensors.getModeBatPorcent() == mService.sensors.MODE_DISABLE) {
                linLayBatPorcent.setVisibility(View.GONE);
            } else {
                linLayBatPorcent.setVisibility(View.VISIBLE);
                isBattFullInvisible = false;
            }

            if (mService.sensors.getModeBatTemp() == mService.sensors.MODE_DISABLE) {
                linLayBatTemp.setVisibility(View.GONE);
            } else {
                linLayBatTemp.setVisibility(View.VISIBLE);
                isBattFullInvisible = false;
            }

            if (mService.sensors.getModeBatSaud() == mService.sensors.MODE_DISABLE) {
                linLayBatSaud.setVisibility(View.GONE);
            } else {
                linLayBatSaud.setVisibility(View.VISIBLE);
                isBattFullInvisible = false;
            }

            if (isBattFullInvisible) {
                linLayBat.setVisibility(View.GONE);
            }else {
                linLayBat.setVisibility(View.VISIBLE);
            }


            Boolean isCarrFullInvisible = true;
            if (mService.sensors.getModeCarrVolt() == mService.sensors.MODE_DISABLE) {
                linLayCarrVolt.setVisibility(View.GONE);
            } else {
                linLayCarrVolt.setVisibility(View.VISIBLE);
                isCarrFullInvisible = false;
            }

            if (mService.sensors.getModeCarrAmp() == mService.sensors.MODE_DISABLE) {
                linLayCarrAmp.setVisibility(View.GONE);
            } else {
                linLayCarrAmp.setVisibility(View.VISIBLE);
                isCarrFullInvisible = false;
            }

            if (mService.sensors.getModeCarrUSB() == mService.sensors.MODE_DISABLE) {
                linLayCarrUSB.setVisibility(View.GONE);
            } else {
                linLayCarrUSB.setVisibility(View.VISIBLE);
                isCarrFullInvisible = false;
            }

            if (mService.sensors.getModeCarrFont() == mService.sensors.MODE_DISABLE) {
                linLayCarrFont.setVisibility(View.GONE);
            } else {
                linLayCarrFont.setVisibility(View.VISIBLE);
                isCarrFullInvisible = false;
            }

            if (mService.sensors.getModeCarrWireless() == mService.sensors.MODE_DISABLE) {
                linLayCarrWireless.setVisibility(View.GONE);
            } else {
                linLayCarrWireless.setVisibility(View.VISIBLE);
                isCarrFullInvisible = false;
            }

            if (isCarrFullInvisible) {
                linLayCarr.setVisibility(View.GONE);
            }else{
                linLayCarr.setVisibility(View.VISIBLE);
            }
        }
    };

    //__________________________________Server______________________________________________
    public void connectService() {
        // Bind to LocalService
        //startService(new Intent(getBaseContext(), ServerSensor.class));
        Intent intent = new Intent(this, ServerSensor.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void startServer() {
        if (!notificationEnable) {
            Intent intent = new Intent(getBaseContext(), ServerSensor.class);
            intent.putExtra("notification", false);
            startService(intent);
            Log.d(TAG, intent + "");
        }
    }

    // Method to stop the service
    public void stopService() {
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        if (!notificationEnable)
            stopService(new Intent(getBaseContext(), ServerSensor.class));
    }

    /*Runnable notificationInit = new Runnable() {
        @Override
        public void run() {
            if(!notificationEnable){
                mService.notificationRm();
            }
        }
    };*/

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ServerSensor.LocalBinder binder = (ServerSensor.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            notificationEnable = mService.isNotificationEnable();
            mService.refresh.post(refresh);
            mService.refresh.post(viewVisibleRefresh);
            startServer();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService.refresh.removeCallbacks(refresh);
            mBound = false;
        }
    };


}
