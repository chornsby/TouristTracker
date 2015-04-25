package com.chornsby.touristtracker.settings;


import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;

import com.chornsby.touristtracker.Constants;
import com.chornsby.touristtracker.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class LocationSettingsHelper implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<LocationSettingsResult> {

    private static final LocationRequest LOCATION_REQUEST = Utility.createNewLocationRequest();

    private Activity mActivity;
    private GoogleApiClient mGoogleClient;

    public LocationSettingsHelper(Activity context) {
        mActivity = context;
    }

    public void checkSettings() {
        GooglePlayServicesHelper mPlayServicesHelper = new GooglePlayServicesHelper(mActivity);

        if (!mPlayServicesHelper.checkPlayServices()) return;

        mGoogleClient = new GoogleApiClient.Builder(mActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mActivity == null) return;

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(LOCATION_REQUEST);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleClient, builder.build());

        result.setResultCallback(this);
    }

    @Override
    public void onResult(LocationSettingsResult result) {
        final Status status = result.getStatus();

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(
                            mActivity, Constants.REQUEST_CHECK_SETTINGS
                    );
                } catch (IntentSender.SendIntentException e) {}
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

}
