package com.chornsby.touristtracker.reminders;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.chornsby.touristtracker.MainActivity;
import com.chornsby.touristtracker.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    private static void showNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, makeNotification(context));
    }

    private static Notification makeNotification(Context context) {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                14,
                new Intent(context, MainActivity.class),
                0
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setColor(context.getResources().getColor(R.color.tt_primary))
                .setSmallIcon(R.drawable.ic_action_my_location)
                .setContentTitle(context.getText(R.string.app_name))
                .setContentText(context.getText(R.string.notif_please_use_the_app))
                .setContentIntent(pendingIntent);

        return builder.build();
    }
}
