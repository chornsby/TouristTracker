package com.chornsby.touristtracker.data;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.chornsby.touristtracker.MainActivity;
import com.chornsby.touristtracker.R;
import com.chornsby.touristtracker.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class TrackerService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG = TrackerService.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mTrackerIntent;
    private static final int REQUEST_CODE = 42;
    private static final int FOREGROUND_ID = 42;
    public static final String ACTION_CLOSE = "stop";

    public TrackerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Build Google Api Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove connection from Google Api Client
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            try {
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                        mGoogleApiClient, mTrackerIntent
                );
            } catch (NullPointerException ignored) {
                // The updates were killed by a user removing the location permission
            }

            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, mTrackerIntent
                );
            } catch (NullPointerException ignored) {
                // The updates were killed by a user removing the location permission
            }

            mGoogleApiClient.disconnect();
        }

        mGoogleApiClient = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        if (action != null && action.equals(ACTION_CLOSE)) {
            setIsTracking(false);
            stopSelf();
            return START_NOT_STICKY;
        }

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        setIsTracking(true);

        startForeground(FOREGROUND_ID, getNotification());

        return START_REDELIVER_INTENT;
    }

    private Notification getNotification() {
        // Build the Notification for the foreground Service
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setColor(getResources().getColor(R.color.tt_primary))
                .setSmallIcon(R.drawable.ic_action_my_location)
                .setContentTitle(getText(R.string.app_name));

        builder.setContentText(getText(R.string.notif_text_tracking));

        builder.addAction(
                R.drawable.ic_action_close,
                getString(R.string.action_stop_tracking),
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
    public void onConnected(Bundle bundle) {
        // Handle permission removed while tracking in progress
        if (Utility.isLocationPermissionRequired(getApplicationContext())) {
            setIsTracking(false);
            stopSelf();
            return;
        }

        LocationRequest request = Utility.createNewLocationRequest();

        Intent intent = new Intent(this, TrackerReceiver.class);
        mTrackerIntent = PendingIntent.getBroadcast(
                getApplicationContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient, 60000, mTrackerIntent
        );
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, request, mTrackerIntent
        );
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setIsTracking(boolean isTracking) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(getString(R.string.pref_track_location), isTracking);
        editor.apply();
    }
}
