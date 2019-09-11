package com.app.rnr.batinfo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by RNR on 07/01/2018.
 */

public class NotificationActivity extends NotificationCompat.Builder {

    private final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    public NotificationActivity(Context context, Class Activity, RemoteViews contentView) {
        super(context);

        Intent intent = new Intent(context, Activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        this.setContentIntent(pendingIntent);
        this.setTicker("BatInfo");
        this.setSmallIcon(R.drawable.bat_notif_b);
        this.setAutoCancel(false);
        this.setOngoing(true);
        this.setCustomContentView(contentView);
        if (Build.VERSION.SDK_INT > 20) {
            this.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        this.build();

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }


    public void notificationVisible(RemoteViews contentView) {
        this.setCustomContentView(contentView);
        notificationManager.notify(NOTIFICATION_ID, this.build());
    }

    public void notificationHide() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

}
