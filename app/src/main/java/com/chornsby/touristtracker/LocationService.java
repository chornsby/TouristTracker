package com.chornsby.touristtracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG = LocationService.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mLocationIntent;
    private static final int REQUEST_CODE = 42;
    private static final int FOREGROUND_ID = 42;
    private static final String ACTION_CLOSE = "stop";

    public LocationService() {
    }

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
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, mLocationIntent
            );
            mGoogleApiClient.disconnect();
        }

        mGoogleApiClient = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        if (action != null && action.equals(ACTION_CLOSE)) {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

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
        Intent intent = new Intent(this, LocationService.class).setAction(action);
        return PendingIntent.getService(this, 0, intent, 0);
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest request = Utility.createNewLocationRequest();

        Intent intent = new Intent(this, LocationReceiver.class);
        mLocationIntent = PendingIntent.getBroadcast(
                getApplicationContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, request, mLocationIntent
        );
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "Connection to GoogleApiClient failed: " + connectionResult.getErrorCode());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}