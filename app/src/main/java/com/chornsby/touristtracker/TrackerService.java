package com.chornsby.touristtracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.chornsby.touristtracker.data.TrackerContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class TrackerService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static String LOG_TAG = TrackerService.class.getSimpleName();

    private static final int FOREGROUND_ID = 42;

    public static final String ACTION_RESUME = "Resume";
    public static final String ACTION_PAUSE = "Pause";
    public static final String ACTION_CLOSE = "Close";

    private GoogleApiClient mGoogleApiClient;
    private boolean mIsTracking;

    @Override
    public void onCreate() {
        super.onCreate();

        // Build Google Api Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove connection from Google Api Client
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

        mGoogleApiClient = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get the action for this command either from the Intent or SharedPreferences
        String action;

        if (intent == null || intent.getAction() == null) {
            boolean isTracking = Utility.getIsTracking(this);

            if (isTracking) {
                action = ACTION_RESUME;
            } else {
                action = ACTION_PAUSE;
            }

        } else {
            action = intent.getAction();
        }

        // Perform given action and update SharedPreferences
        switch (action) {
            case ACTION_RESUME:
                mIsTracking = true;
                Utility.setIsTracking(this, true);
                resumeTracking();
                break;
            case ACTION_PAUSE:
                mIsTracking = false;
                Utility.setIsTracking(this, false);
                pauseTracking();
                break;
            case ACTION_CLOSE:
                mIsTracking = false;
                Utility.setIsTracking(this, false);
                stopSelf();
                return START_NOT_STICKY;
            default:
                throw new IllegalArgumentException("Unknown action: " + intent.getAction());
        }

        // Start the Service as a foreground process
        startForeground(FOREGROUND_ID, getNotification());

        return START_STICKY;
    }

    private void resumeTracking() {
        // Make connection to Google Api Client
        // TODO: Move this processing off the UI thread using ThreadHandler
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    private void pauseTracking() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private Notification getNotification() {
        // Build the Notification for the foreground Service
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setColor(getResources().getColor(R.color.tt_primary))
                .setSmallIcon(R.drawable.ic_action_my_location)
                .setContentTitle(getText(R.string.app_name));

        if (mIsTracking) {
            builder.setContentText(getText(R.string.notif_text_tracking))
                   .addAction(
                           R.drawable.ic_action_location_off,
                           getString(R.string.action_tracking_pause),
                           getServicePendingIntent(ACTION_PAUSE)
                   );
        } else {
            builder.setContentText(getText(R.string.notif_text_not_tracking))
                   .addAction(
                           R.drawable.ic_action_location_on,
                           getString(R.string.action_tracking_resume),
                           getServicePendingIntent(ACTION_RESUME)
                   );
        }

        builder.addAction(
                R.drawable.ic_action_close,
                getString(R.string.action_tracking_close),
                getServicePendingIntent(ACTION_CLOSE)
        );

        builder.setContentIntent(getActivityPendingIntent());

        return builder.build();
    }

    private PendingIntent getActivityPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    private PendingIntent getServicePendingIntent(String action) {
        Intent intent = new Intent(this, TrackerService.class).setAction(action);
        return PendingIntent.getService(this, 0, intent, 0);
    }

    @Override
    public void onLocationChanged(Location location) {
        // Store Location data in the database
        ContentValues locationValues = Utility.getContentValuesFromLocation(location);
        getContentResolver().insert(TrackerContract.LocationEntry.CONTENT_URI, locationValues);
        getContentResolver().notifyChange(TrackerContract.BASE_CONTENT_URI, null);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Register for Location updates
        LocationRequest request = Utility.createNewLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "GooglePlayServices connection suspended");
        Log.d(LOG_TAG, "Trying to reconnect");

        // Try to reconnect
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "GooglePlayServices connection failed: " + connectionResult.getErrorCode());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
