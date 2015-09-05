package com.chornsby.touristtracker.submit;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.chornsby.touristtracker.MainActivity;
import com.chornsby.touristtracker.R;

import java.io.File;

public class DataUploadService extends IntentService {

    public static final String EXTRA_USER_EMAIL = "user_email";

    private static final String LOG_TAG = DataUploadService.class.getSimpleName();
    private static final String WORKER_THREAD = DataUploadService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 43;

    private String mUserEmail;
    private Handler mMainThreadHandler;

    public DataUploadService() {
        super(WORKER_THREAD);
        mMainThreadHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mUserEmail = intent.getStringExtra(EXTRA_USER_EMAIL);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = getNotificationBuilder();

        String notificationText;

        // Set indeterminate progress bar
        builder.setProgress(0, 0, true);
        builder.setOngoing(true);
        builder.setAutoCancel(false);

        // Set uploading content text
        notificationText = getString(R.string.notif_text_uploading);
        builder.setContentText(notificationText);

        // Notify the user that data upload has begun
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        sendToastToMainThread(notificationText);

        // Perform the data upload
        boolean success = uploadData();

        // Finish progress
        builder.setProgress(0, 0, false);
        builder.setOngoing(false);
        builder.setAutoCancel(true);

        // Set the appropriate resolution message
        if (success) {
            notificationText = getString(R.string.notif_text_upload_success);
        } else {
            notificationText = getString(R.string.notif_text_upload_failure);
        }

        builder.setContentText(notificationText);

        // Update the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        sendToastToMainThread(notificationText);

        if (success) {
            // Prompt the user to uninstall the app
            Uri packageUri = Uri.parse("package:" + getString(R.string.content_authority));
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(uninstallIntent);
        }
    }

    private boolean uploadData() {
        // Assume that everything works until proven wrong
        boolean success = true;

        File activityFile = null;
        File locationFile = null;
        File notesFile = null;

        // Upload the user data to the server
        try {
            activityFile = JsonGenerator.generateActivityJsonFile(this);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            Log.e(LOG_TAG, "Error uploading activity.json");
        }

        try {
            locationFile = JsonGenerator.generateLocationJsonFile(this);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            Log.e(LOG_TAG, "Error uploading location.json");
        }

        try {
            notesFile = JsonGenerator.generateNotesJsonFile(this);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            Log.e(LOG_TAG, "Error uploading notes.json");
        }

        FileUploader.tryUploadFile(this, activityFile, mUserEmail);
        FileUploader.tryUploadFile(this, locationFile, mUserEmail);
        FileUploader.tryUploadFile(this, notesFile, mUserEmail);

        return success;
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        return new NotificationCompat.Builder(this)
                .setColor(getResources().getColor(R.color.tt_primary))
                .setSmallIcon(R.drawable.ic_action_my_location)
                .setContentTitle(getText(R.string.app_name))
                .setContentIntent(getActivityPendingIntent());
    }

    private PendingIntent getActivityPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    private void sendToastToMainThread(final String message) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DataUploadService.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
