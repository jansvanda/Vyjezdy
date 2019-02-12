package com.hans.svandasek.fire.vyjezdy.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.hans.svandasek.fire.vyjezdy.R;
import com.hans.svandasek.fire.vyjezdy.ui.activities.ArticleActivity;
import com.hans.svandasek.fire.vyjezdy.ui.activities.MyApplication;
import com.hans.svandasek.fire.vyjezdy.ui.activities.NotificationArticleActivity;
import com.hans.svandasek.fire.vyjezdy.ui.activities.SettingsActivity;

public class NotificationHelper {

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10051";

    public NotificationHelper(Context context) {
       mContext = context;
    }


    /**
     * Create and push the notification
     */
    public void createNotification(String title, String time) {

        MyApplication.getAppContext();


        Intent resultIntent = new Intent(mContext , NotificationArticleActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationManager = (NotificationManager) MyApplication.getAppContext().getSystemService(mContext.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_firee)
                        .setContentTitle(title)
                        .setContentText(time)
                        .setContentIntent(notifyPendingIntent)
                        .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Nový výjezd HZS JMK", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }
}