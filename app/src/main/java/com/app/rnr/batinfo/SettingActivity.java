package com.app.rnr.batinfo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by RNR on 29/01/2018.
 */

public class SettingActivity extends AppCompatActivity {
    final String TAG = this.getClass().getName();

    LinearLayout linLayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        linLayList = (LinearLayout) findViewById(R.id.LinLayList);

        LinearLayout.LayoutParams linLay = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView t = new TextView(this);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP,30f);
        t.setText("Sensores");
        t.setLayoutParams(linLay);
        t.setGravity(Gravity.CENTER);
        linLayList.addView(t);

        linLay = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,5);
        View v = new View(this);
        linLay.height = 1;
        v.setLayoutParams(linLay);
        v.setBackgroundColor(Color.WHITE);
        linLayList.addView(v);


        linLayList.addView(item("ola mundo","0.000","3,154","File","/sys/asd/dd"));
        linLayList.addView(item("ola mundo","0.000","3,154","File","/sys/asd/dd"));
        linLayList.addView(item("ola mundo","0.000","3,154","File","/sys/asd/dd"));
        linLayList.addView(item("ola mundo","0.000","3,154","File","/sys/asd/dd"));

        /*for (int i = 0; i < 500; i++) {
            TextView t = new TextView(this);
            t.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
            t.setText("Item: " + i);
            linLayList.addView(t);
        }*/


    }



    private View item(String sName, String sBase, String sValor, String sMode, String sFile){
        int dip = (int)(20f * getResources().getDisplayMetrics().density);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout lv = new LinearLayout(this);
        lv.setOrientation(LinearLayout.VERTICAL);
        lv.setLayoutParams(lp);
        lv.setClickable(true);
        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Click");
            }
        });


        LinearLayout lh = new LinearLayout(this);
        lh.setOrientation(LinearLayout.HORIZONTAL);
        lh.setLayoutParams(lp);


        TextView t = new TextView(this);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
        t.setText(sName);
        t.setPadding(dip,0,dip,0);
        lh.addView(t);

        LinearLayout lt = new LinearLayout(this);
        lt.setLayoutParams(lp);
        lt.setGravity(Gravity.RIGHT);


        t = new TextView(this);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
        t.setText(sBase);
        t.setPadding(dip,0,dip,0);
        lt.addView(t);


        t = new TextView(this);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
        t.setText(sValor);
        t.setPadding(dip,0,dip,0);
        lt.addView(t);

        lh.addView(lt);

        lv.addView(lh);


        lh = new LinearLayout(this);
        lh.setOrientation(LinearLayout.HORIZONTAL);
        lh.setLayoutParams(lp);

        t = new TextView(this);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
        t.setText("Modo: " + sMode);
        t.setPadding(dip,0,dip,0);
        lh.addView(t);

        lt = new LinearLayout(this);
        lt.setLayoutParams(lp);
        lt.setGravity(Gravity.RIGHT);


        t = new TextView(this);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
        t.setText(sFile);
        t.setPadding(dip,0,dip,0);
        lt.addView(t);

        lh.addView(lt);

        lv.addView(lh);

        LinearLayout.LayoutParams linLay = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,5);
        View v = new View(this);
        linLay.height = 1;
        v.setLayoutParams(linLay);
        v.setBackgroundColor(Color.WHITE);
        lv.addView(v);

        return lv;
    }
}
