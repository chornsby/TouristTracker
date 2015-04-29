package com.chornsby.touristtracker;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.chornsby.touristtracker.data.TrackerContract;
import com.google.android.gms.location.FusedLocationProviderApi;

public class LocationReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = LocationReceiver.class.getSimpleName();

    public LocationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Receive the broadcast Location object
        Location location = (Location) intent.getExtras().get(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

        if (location == null) {
            Log.e(LOG_TAG, "Received null data from intent");
            return;
        }

        // Parse Location into database-friendly ContentValues
        ContentValues locationValues = Utility.getContentValuesFromLocation(location);

        // Get the ContentResolver to store the data in the database
        ContentResolver contentResolver = context.getContentResolver();

        // Store Location data in the database
        contentResolver.insert(TrackerContract.LocationEntry.CONTENT_URI, locationValues);
    }
}
